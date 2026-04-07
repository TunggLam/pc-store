package com.example.pcstore.service.impl;

import com.example.pcstore.constant.Constant;
import com.example.pcstore.entity.UserProfile;
import com.example.pcstore.exception.BusinessException;
import com.example.pcstore.logging.LoggingFactory;
import com.example.pcstore.mapper.UserProfileMapper;
import com.example.pcstore.model.request.UpdateProfileRequest;
import com.example.pcstore.model.response.UserProfileResponse;
import com.example.pcstore.repositories.UserProfileRepository;
import com.example.pcstore.service.UserProfileService;
import com.example.pcstore.utils.JWTUtils;
import com.example.pcstore.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

    private static final Logger LOGGER = LoggingFactory.getLogger(UserProfileServiceImpl.class);

    private final UserProfileRepository userProfileRepository;
    private final UserProfileMapper userProfileMapper;

    @Override
    public UserProfile findUserProfileByUsername(String username) {
        return userProfileRepository.findByUsername(username).orElse(null);
    }

    @Override
    public UserProfileResponse myProfile() {
        String username = JWTUtils.getUsername();
        UserProfile userProfile = findUserProfileByUsername(username);
        return userProfileMapper.mapToUserProfileResponse(userProfile);
    }

    @Override
    public UserProfileResponse updateProfile(UpdateProfileRequest request) {
        String username = JWTUtils.getUsername();
        LOGGER.info("[USER PROFILE][UPDATE][{}] Bắt đầu cập nhật hồ sơ", username);

        UserProfile userProfile = findUserProfileByUsername(username);
        if (userProfile == null) {
            throw new BusinessException(Constant.USER_NOT_EXIST);
        }

        if (StringUtils.isNotNullOrEmpty(request.getEmail())
                && !request.getEmail().equalsIgnoreCase(userProfile.getEmail())
                && Boolean.TRUE.equals(userProfileRepository.existsByEmailAndUsernameNot(request.getEmail(), username))) {
            LOGGER.warn("[USER PROFILE][UPDATE][{}] Email {} đã được sử dụng", username, request.getEmail());
            throw new BusinessException(Constant.EMAIL_EXISTS);
        }

        if (StringUtils.isNotNullOrEmpty(request.getPhoneNumber())
                && !request.getPhoneNumber().equals(userProfile.getPhoneNumber())
                && Boolean.TRUE.equals(userProfileRepository.existsByPhoneNumberAndUsernameNot(request.getPhoneNumber(), username))) {
            LOGGER.warn("[USER PROFILE][UPDATE][{}] Số điện thoại {} đã được sử dụng", username, request.getPhoneNumber());
            throw new BusinessException(Constant.PHONE_EXISTS);
        }

        if (request.getFirstName() != null) userProfile.setFirstName(request.getFirstName());
        if (request.getLastName() != null) userProfile.setLastName(request.getLastName());
        if (request.getEmail() != null) userProfile.setEmail(request.getEmail());
        if (request.getPhoneNumber() != null) userProfile.setPhoneNumber(request.getPhoneNumber());
        if (request.getAddress() != null) userProfile.setAddress(request.getAddress());

        userProfileRepository.save(userProfile);
        LOGGER.info("[USER PROFILE][UPDATE][{}] Cập nhật hồ sơ thành công", username);

        return userProfileMapper.mapToUserProfileResponse(userProfile);
    }

    @Override
    public String getFullNameByUsername(String username) {
        UserProfile userProfile = findUserProfileByUsername(username);
        if (userProfile == null) {
            return null;
        }
        return userProfile.getFirstName() + " " + userProfile.getLastName();
    }
}

