package com.example.pcstore.service.impl;

import com.example.pcstore.constant.Constant;
import com.example.pcstore.entity.UserProfile;
import com.example.pcstore.entity.UserProfileOTP;
import com.example.pcstore.enums.OTPTypeEnum;
import com.example.pcstore.exception.BusinessException;
import com.example.pcstore.logging.LoggingFactory;
import com.example.pcstore.model.request.SendOTPRequest;
import com.example.pcstore.model.request.VerifyOTPForgotPasswordRequest;
import com.example.pcstore.redis.RedisService;
import com.example.pcstore.repositories.UserProfileOTPRepository;
import com.example.pcstore.repositories.UserProfileRepository;
import com.example.pcstore.service.OTPService;
import com.example.pcstore.utils.PasswordUtils;
import com.example.pcstore.utils.RegexUtils;
import com.example.pcstore.utils.StringUtils;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OTPServiceImpl implements OTPService {

    private static final Logger LOGGER = LoggingFactory.getLogger(OTPServiceImpl.class);

    private static final String CONTENT_REGISTER = "<h2>Mã xác thực đăng ký tại PC Store</h2><p>Xin chào: %s,</p><p>Mã OTP của bạn là: <strong>%s</strong></p><p>Vui lòng sử dụng mã OTP này để xác thực đăng ký tài khoản của bạn</p><p>Lưu ý không chia sẻ mã OTP này cho bất kỳ ai.</p><br><p>Trân trọng,</p><p>Ecommerce</p>";
    private static final String CONTENT_FORGOT_PASSWORD = "<h2>Mã xác thực quên mật khẩu tại PC Store</h2><p>Xin chào: %s,</p><p>Mã OTP của bạn là: <strong>%s</strong></p><p>Vui lòng sử dụng mã OTP này để xác thực và tạo mật khẩu mới cho tài khoản của bạn</p><p>Lưu ý không chia sẻ mã OTP này cho bất kỳ ai.</p><br><p>Trân trọng,</p><p>Ecommerce</p>";
    private static final String SUBJECT = "Mã xác thực đăng ký tài khoản tại PC Store";
    private static final int TOTAL_RETRY_PER_DAY = 3;
    private static final int TOTAL_FALSE_OTP = 5;

    private final UserProfileOTPRepository userProfileOTPRepository;
    private final UserProfileRepository userProfileRepository;
    private final JavaMailSender mailSender;
    private final RedisService redisService;

    @Value("${spring.mail.username}")
    private String usernameEmail;

    @Override
    public void sendOTP(SendOTPRequest request) {
        /* Kiểm tra Email có đúng định dạng hay không, và username */
        validateRequest(request);

        String username = request.getUsername();

        /* Nếu tồn tại thông tin user thì dừng luồng */
        validateByType(request);

        /* Kiểm tra số lần gửi OTP trong 1 ngày */
        validateTotalOTPInDay(username, request.getType().name());

        /* Kiểm tra đã quá số lần verify fail hay chưa */
        validateCountVerifyOTP(request);

        /* Inactive tất cả OTP đang còn hiệu lực nếu cấp mới OTP */
        userProfileOTPRepository.inactiveAllStatus(username, request.getType().name());

        String otp = generateOTP();
        LOGGER.info("[OTP][{}][SEND OTP] Mã OTP: {}", username, otp);

        /* Gửi Email đến người dùng */
        sendEmail(request, username, otp);

        /* Lưu thông tin OTP để verify */
        saveUserProfileOTP(request, otp);
    }

    @Override
    public void verifyOTPForgotPassword(VerifyOTPForgotPasswordRequest request) {
        UserProfile userProfile = userProfileRepository.findByEmail(request.getEmail()).orElse(null);
        if (Objects.isNull(userProfile)) {
            throw new BusinessException(Constant.USER_NOT_EXIST);
        }

        String username = userProfile.getUsername();

        /* Kiểm tra xem có đúng là người nhận được otp tạo tài khoản hay không. */
        UserProfileOTP userProfileOTP = userProfileOTPRepository.getLatestOTP(username, OTPTypeEnum.FORGOT_PASSWORD.name());
        LOGGER.info("[OTP][VERIFY][{}][FORGOT PASSWORD] User Profile OTP: {}", username, userProfileOTP);

        if (Objects.isNull(userProfileOTP)) {
            throw new BusinessException(Constant.OTP_EXPIRED_OR_INVALID_MES);
        }

        int countVerifyFail = Objects.isNull(userProfileOTP.getCountVerifyFalse()) ? 0 : userProfileOTP.getCountVerifyFalse();
        LOGGER.info("[OTP][VERIFY][{}][FORGOT PASSWORD] Số lần verify OTP lỗi: {}", username, countVerifyFail);

        if (Boolean.FALSE.equals(userProfileOTP.getStatus()) && countVerifyFail >= TOTAL_FALSE_OTP) {
            throw new BusinessException(Constant.VERIFY_OTP_5TH);
        }

        setChecksumKey(request);
        checkEqualsOTP(request.getOtp(), userProfileOTP, countVerifyFail);
    }

    private void setChecksumKey(VerifyOTPForgotPasswordRequest request) {
        String checksum = PasswordUtils.endCodeMD5(request.getEmail() + request.getOtp());
        String key = request.getEmail() + "_FP";
        redisService.set(key, checksum, 600L);
    }

    private static void validateRequest(SendOTPRequest request) {
        if (RegexUtils.matches(request.getEmail(), RegexUtils.EMAIL)) {
            throw new BusinessException("Định dạng Email không hợp lệ");
        }

        if (StringUtils.equals(request.getType().name(), OTPTypeEnum.REGISTER.name()) && StringUtils.isNullOrEmpty(request.getUsername())) {
            throw new BusinessException("Username không được để trống");
        }
    }

    private void validateByType(SendOTPRequest request) {
        /* Nếu type là gửi OTP đăng kí tài khoản thì kiểm tra xem email, username, sdt có tồn tại hay không */
        validateIfRegisterUser(request);

        validateIfForgotPassword(request);
    }

    private void validateIfForgotPassword(SendOTPRequest request) {
        if (StringUtils.equals(request.getType().name(), OTPTypeEnum.FORGOT_PASSWORD.name()) && Boolean.FALSE.equals(userProfileRepository.existsByEmail(request.getEmail()))) {
            throw new BusinessException(Constant.EMAIL_NOT_EXISTS, "EMAIL_EXISTS");
        }
    }

    private void validateIfRegisterUser(SendOTPRequest request) {
        if (StringUtils.equals(request.getType().name(), OTPTypeEnum.REGISTER.name())) {
            Optional<UserProfile> userProfileOptional = userProfileRepository.findByUsername(request.getUsername());
            if (userProfileOptional.isPresent()) {
                throw new BusinessException(Constant.USERNAME_EXISTS, "USERNAME_EXISTS");
            }

            if (Boolean.TRUE.equals(userProfileRepository.existsByEmail(request.getEmail()))) {
                throw new BusinessException(Constant.EMAIL_EXISTS, "EMAIL_EXISTS");
            }

            if (Boolean.TRUE.equals(userProfileRepository.existsByPhoneNumber(request.getPhoneNumber()))) {
                throw new BusinessException(Constant.PHONE_EXISTS, "PHONE_EXISTS");
            }
        }
    }

    private void validateCountVerifyOTP(SendOTPRequest request) {
        UserProfileOTP userProfileOTP = userProfileOTPRepository.getLatestOTP(request.getUsername(), request.getType().name());
        if (Objects.nonNull(userProfileOTP)) {
            int countVerifyFail = userProfileOTP.getCountVerifyFalse() == null ? 0 : userProfileOTP.getCountVerifyFalse();
            if (Boolean.FALSE.equals(userProfileOTP.getStatus())
                    && Objects.nonNull(userProfileOTP.getLastVerifyAt())
                    && countVerifyFail >= TOTAL_FALSE_OTP
                    && compareTimeVerify(userProfileOTP.getLastVerifyAt())) {
                throw new BusinessException(String.format(Constant.VERIFY_OTP_BLOCKED_MESS, TOTAL_FALSE_OTP));
            }
        }
    }

    private void validateTotalOTPInDay(String username, String type) {
        int totalOTPToday = userProfileOTPRepository.getTotalOTPToday(username, type);
        if (totalOTPToday >= TOTAL_RETRY_PER_DAY) {
            throw new BusinessException(Constant.OTP_EXCEEDED);
        }
    }

    private boolean compareTimeVerify(LocalDateTime verifyAt) {
        /* Lấy thời gian hiện tại */
        LocalDateTime now = LocalDateTime.now();

        /* Tính khoảng cách thời gian giữa thời gian hiện tại và thời gian cần so sánh */
        Duration duration = Duration.between(verifyAt, now);

        /* Kiểm tra xem khoảng cách này có quá 5 phút hay không */
        return Math.abs(duration.toMillis()) <= 300000;
    }

    private void sendEmail(SendOTPRequest request, String username, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            message.setFrom(new InternetAddress(usernameEmail));
            message.setRecipients(Message.RecipientType.TO, request.getEmail());
            message.setSubject(SUBJECT);
            if (StringUtils.equals(request.getType().name(), OTPTypeEnum.REGISTER.name())) {
                message.setContent(String.format(CONTENT_REGISTER, request.getUsername(), otp), "text/html; charset=utf-8");
            } else {
                username = userProfileRepository.getUsernameByEmail(request.getEmail());
                message.setContent(String.format(CONTENT_FORGOT_PASSWORD, username, otp), "text/html; charset=utf-8");
            }

            mailSender.send(message);
        } catch (MessagingException e) {
            LOGGER.error("[OTP][{}][SEND OTP] Có lỗi khi gửi Email. Exception: {}", username, e.getMessage());
            throw new BusinessException(Constant.EXCEPTION_MESSAGE_DEFAULT);
        }
    }

    private void saveUserProfileOTP(SendOTPRequest request, String otp) {
        UserProfileOTP userProfileOtp = new UserProfileOTP();
        userProfileOtp.setOtp(otp);
        if (StringUtils.equals(request.getType().name(), OTPTypeEnum.REGISTER.name())) {
            userProfileOtp.setUsername(request.getUsername());
        } else {
            userProfileOtp.setUsername(userProfileRepository.getUsernameByEmail(request.getEmail()));
        }
        userProfileOtp.setType(request.getType().name());
        userProfileOtp.setStatus(true);
        userProfileOtp.setCountVerifyFalse(0);
        userProfileOTPRepository.save(userProfileOtp);
    }

    private void checkEqualsOTP(String otp, UserProfileOTP userProfileOTP, int countVerifyFail) {
        if (StringUtils.notEquals(userProfileOTP.getOtp(), otp)) {
            userProfileOTP.setCountVerifyFalse(countVerifyFail + 1);
            userProfileOTP.setLastVerifyAt(LocalDateTime.now());
            userProfileOTPRepository.save(userProfileOTP);
            throw new BusinessException(Constant.OTP_EXPIRED_OR_INVALID_MES);
        } else {
            userProfileOTP.setIsVerified(true);
            userProfileOTP.setVerifyAt(LocalDateTime.now());
            userProfileOTP.setLastVerifyAt(LocalDateTime.now());
            userProfileOTPRepository.save(userProfileOTP);
        }
    }

    private static String generateOTP() {
        int otp = new SecureRandom().nextInt(999999);
        return String.format("%06d", otp);
    }
}
