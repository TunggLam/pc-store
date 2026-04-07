package com.example.pcstore.service.impl;
import com.example.pcstore.constant.Constant;
import com.example.pcstore.entity.Cart;
import com.example.pcstore.entity.CartItem;
import com.example.pcstore.entity.PaymentVNPay;
import com.example.pcstore.exception.BusinessException;
import com.example.pcstore.logging.LoggingFactory;
import com.example.pcstore.model.proxy.request.VNPayQueryOrderRequest;
import com.example.pcstore.model.proxy.response.VNPayQueryOrderResponse;
import com.example.pcstore.model.request.VNPayCallbackRequest;
import com.example.pcstore.model.request.VNPayParameters;
import com.example.pcstore.model.response.CallbackResponse;
import com.example.pcstore.model.response.VNPayIPNResponse;
import com.example.pcstore.model.response.VNPayInitOrderResponse;
import com.example.pcstore.proxy.VNPayProxy;
import com.example.pcstore.entity.PaymentHistory;
import com.example.pcstore.repositories.CartItemRepository;
import com.example.pcstore.repositories.CartRepository;
import com.example.pcstore.repositories.PaymentHistoryRepository;
import com.example.pcstore.repositories.PaymentVNPayRepository;
import com.example.pcstore.service.PaymentService;
import com.example.pcstore.utils.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static jodd.util.StringPool.UNDERSCORE;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private static final Logger LOGGER = LoggingFactory.getLogger(PaymentServiceImpl.class);

    private final CartRepository cartRepository;
    private final PaymentVNPayRepository paymentVNPayRepository;
    private final PaymentHistoryRepository paymentHistoryRepository;
    private final ObjectMapper objectMapper;
    private final VNPayProxy vnPayProxy;
    private final CartItemRepository cartItemRepository;
//    private final TelegramProxy telegramProxy;

    private static final String VERSION_PARAM = "vnp_Version";
    private static final String COMMAND_PARAM = "vnp_Command";
    private static final String EXPIRE_DATE_PARAM = "vnp_ExpireDate";
    private static final String CREATE_DATE_PARAM = "vnp_CreateDate";
    private static final String IP_ADDR_PARAM = "vnp_IpAddr";
    private static final String RETURN_URL_PARAM = "vnp_ReturnUrl";
    private static final String LOCALE_PARAM = "vnp_Locale";
    private static final String ORDER_TYPE_PARAM = "vnp_OrderType";
    private static final String ORDER_INFO_PARAM = "vnp_OrderInfo";
    private static final String TXN_REF_PARAM = "vnp_TxnRef";
    private static final String BANK_CODE_PARAM = "vnp_BankCode";
    private static final String TMN_CODE_PARAM = "vnp_TmnCode";
    private static final String AMOUNT_PARAM = "vnp_Amount";
    private static final String CURR_CODE_PARAM = "vnp_CurrCode";
    private static final String SECURE_HASH_PARAM = "vnp_SecureHash";
    private static final String SECURE_HASH_TYPE = "vnp_SecureHashType";
    private static final String VND = "VND";
    private static final String OTHER = "other";
    private static final String VN = "vn";
    private static final String VN_BANK = "VNBANK";
    private static final String DEFAULT_IP = "127.0.0.1";

    @Value("${vnpay.version}")
    private String version;

    @Value("${vnpay.command}")
    private String command;

    @Value("${vnpay.tmn-code}")
    private String tmnCode;

    @Value("${vnpay.domain}")
    private String domain;

    @Value("${vnpay.hash-secret}")
    private String hashSecret;

    @Value("${vnpay.callback-url}")
    private String callbackUrl;

    @Override
    public VNPayInitOrderResponse initOrder() {
        String username = JWTUtils.getUsername();

        /*-- Validate số tiền thanh toán --*/
        Cart cart = cartRepository.getCartPending(username);
        if (cart == null) {
            throw new BusinessException("Không tìm thấy thông tin đơn hàng");
        }

        List<CartItem> cartItems = cartItemRepository.getCartItems(username, cart.getId());
        List<BigDecimal> prices = cartItems.stream().map(CartItem::getTotalAmount).toList();

        int totalPrice = prices.stream().reduce(BigDecimal.ZERO, BigDecimal::add).intValue();
        LOGGER.info("[PAYMENT][VNPAY][INIT][{}] Tổng số tiền của đơn hàng: {}", username, totalPrice);

        if (totalPrice <= 0) {
            throw new BusinessException("Giá trị đơn hàng không hợp lệ");
        }

        /*-- Mã đơn hàng --*/
        String invoiceNo = "PCSTORE" + username.toUpperCase() + (int) (System.currentTimeMillis() / 1000);
        LOGGER.info("[PAYMENT][VNPAY][INIT][{}] Mã đơn hàng thanh toán: {}", username, invoiceNo);

        /*-- Thông tin mô  tả đơn hàng --*/
        String orderInfo = "PCStore - Thanh toan don hang: " + invoiceNo;
        LOGGER.info("[PAYMENT][VNPAY][INIT][{}] Mô tả đơn hàng thanh toán: {}", username, orderInfo);

        /*-- Địa chỉ IP của khách hàng --*/
        String ipAddress = StringUtils.isNullOrEmpty(HttpsUtils.getValueFromHeader(Constant.X_FORWARDED_FOR)) ? DEFAULT_IP : HttpsUtils.getValueFromHeader(Constant.X_FORWARDED_FOR);
        LOGGER.info("[PAYMENT][VNPAY][INIT][{}] Địa chỉ IP V4: {}", username, ipAddress);

        /*-- Thời gian khởi tạo đơn hàng --*/
        String createDate = DateUtils.getDate(DateUtils.YYYYMMDDHHMMSS);
        LOGGER.info("[PAYMENT][VNPAY][INIT][{}] Thời gian tạo đơn hàng: {}", username, createDate);

        /*-- Thời gian hết hạn đơn hàng --*/
        String expireDate = DateUtils.getExpireDate(createDate, DateUtils.YYYYMMDDHHMMSS, 15);
        LOGGER.info("[PAYMENT][VNPAY][INIT][{}] Thời gian hết hạn đơn hàng: {}", username, expireDate);

        /*-- Số tiền khi gửi sang VN Pay thì phải nhân với 100 --*/
        HashMap<String, String> vnpayParams = addParamsToMap(totalPrice, invoiceNo, orderInfo, createDate, expireDate, ipAddress);
        LOGGER.info("[PAYMENT][VNPAY][INIT][{}] Thông tin param khi đưa vào map: {}", username, vnpayParams);

        ArrayList<String> fieldNames = new ArrayList<>(vnpayParams.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        /*-- Encode về US_ASCII và build ra query url của đơn hàng --*/
        encodeAndBuildQueryUrl(vnpayParams, fieldNames, hashData, query);

        /*-- Thông tin query url của đơn hàng --*/
        String queryUrl = query.toString();
        LOGGER.info("[PAYMENT][VNPAY][INIT][{}] Thông tin đường link query đơn hàng: {}", username, queryUrl);

        /*-- Chữ ký của đơn hàng --*/
        String secureHash = HMacUtils.encodeHMacSHA512(hashSecret, hashData.toString());
        LOGGER.info("[PAYMENT][VNPAY][INIT][{}] Secure Hash của đơn hàng: {}", username, secureHash);

        /*-- Đường link thanh toán của đơn hàng --*/
        String target = UriComponentsBuilder.fromHttpUrl(domain).path("/paymentv2/vpcpay.html").query(queryUrl).queryParam(SECURE_HASH_PARAM, secureHash).build().toString();
        LOGGER.info("[PAYMENT][VNPAY][INIT][{}] Đường link thanh toán: {}", username, target);

        /*-- Lưu lại các thông tin thanh toán --*/
        /*-- Lưu lịch sử thanh toán --*/
//        var paymentHistory = buildPaymentHistory(request, username, device, invoiceNo);

        /*-- Lưu thông tin giao dịch vnpay --*/
        PaymentVNPay paymentVNPay = buildPaymentVNPay(totalPrice, expireDate, username, invoiceNo, orderInfo, createDate, ipAddress);

        paymentVNPayRepository.save(paymentVNPay);
        LOGGER.info("[PAYMENT][VNPAY][INIT][{}] Lưu thông tin thanh toán VNPay: {}", username, paymentVNPay);

        /*-- Lưu liên kết đơn hàng ↔ thanh toán để admin có thể tra cứu thông tin payment --*/
        PaymentHistory paymentHistory = new PaymentHistory();
        paymentHistory.setUsername(username);
        paymentHistory.setOrderHistoryId(cart.getId());
        paymentHistory.setPaymentId(paymentVNPay.getId());
        paymentHistoryRepository.save(paymentHistory);
        LOGGER.info("[PAYMENT][VNPAY][INIT][{}] Lưu liên kết đơn hàng: cartId={}, paymentId={}", username, cart.getId(), paymentVNPay.getId());

        return VNPayInitOrderResponse.builder().target(target).txnRef(invoiceNo).build();
    }

    @Override
    public CallbackResponse callback(VNPayCallbackRequest request) {
        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException(Constant.EXCEPTION_MESSAGE_DEFAULT);
        }
        /*--Lấy ra thông tin thanh toán của VNPAY, mục đích để lấy ra trường transactionStatus --*/
        var paymentVNPay = paymentVNPayRepository.getByInvoiceNo(request.getInvoiceNo());
        if (Objects.isNull(paymentVNPay)) {
            throw new BusinessException("Không tìm thấy thông tin thanh toán. Vui lòng thử lại sau.");
        }

        /*-- Request ID  --*/
        var requestId = UUID.randomUUID().toString();
        LOGGER.info("[PAYMENT][VNPAY][CALLBACK] Reqeust ID: {}", requestId);

        /*-- Mô tả thông tin kiểm tra đơn hàng --*/
        var orderInfo = "ECommerce - Tra cuu thong tin don hang: " + paymentVNPay.getInvoiceNo();
        LOGGER.info("[PAYMENT][VNPAY][CALLBACK] Mô tả thông tin yêu cầu query đơn hàng: {}", orderInfo);

        /*-- Địa chỉ Ip của khách hàng --*/
        String ipAddress = StringUtils.isNullOrEmpty(HttpsUtils.getValueFromHeader(Constant.X_FORWARDED_FOR)) ? DEFAULT_IP : HttpsUtils.getValueFromHeader(Constant.X_FORWARDED_FOR);
        LOGGER.info("[PAYMENT][VNPAY][CALLBACK] Địa chỉ IP: {}", ipAddress);

        /*-- Thời gian gửi query đơn hàng --*/
        var createDate = DateUtils.getDate(DateUtils.YYYYMMDDHHMMSS);
        LOGGER.info("[PAYMENT][VNPAY][CALLBACK] Thời gian gửi yêu cầu: {}", createDate);

        /*-- Dãy chữ ký để build ra secureHash --*/
        var checksum = requestId + "|" +
                version + "|" +
                "querydr" + "|" +
                tmnCode + "|" +
                paymentVNPay.getInvoiceNo() + "|" +
                paymentVNPay.getTransactionCreateDate() + "|" +
                createDate + "|" +
                ipAddress + "|" +
                orderInfo;
        LOGGER.info("[PAYMENT][VNPAY][CALLBACK] Dãy checksum: {}", checksum);

        /*-- Build ra mã checksum để bảo toàn dữ liệu khi gửi sang vnpay --*/
        var secureHash = HMacUtils.encodeHMacSHA512(hashSecret, checksum);
        LOGGER.info("[PAYMENT][VNPAY][CALLBACK] Mã chữ ký: {}", secureHash);

        /*-- Build ra request để truy vấn thông tin đơn hàng --*/
        var queryOrderRequest = buildRequestQueryOrder(paymentVNPay, requestId, orderInfo, ipAddress, createDate, secureHash);
        LOGGER.info("[PAYMENT][VNPAY][CALLBACK] Request Query Order: {}", queryOrderRequest);

        /*-- Response của truy vấn thông tin đơn hàng --*/
        var queryOrderResponse = vnPayProxy.queryOrder(queryOrderRequest);
        LOGGER.info("[PAYMENT][VNPAY][CALLBACK] Response Query Order: {}", queryOrderResponse);

        if (Objects.isNull(queryOrderResponse)) {
            throw new BusinessException(Constant.EXCEPTION_MESSAGE_DEFAULT);
        }

        /*-- Thông tin lịch sử thanh toán --*/
//        var paymentHistory = paymentHistoryRepository.findPaymentHistoryByInvoiceNoAndPaymentMethod(request.getTxnRef(), "VNPAY");
//        LOGGER.info("[PAYMENT][VNPAY][CALLBACK] Thông tin lịch sử thanh toán: {}", paymentHistory);

        /* Trường amount sẽ phải + thêm 12k tiền phí để cho FE hiển thị đúng số tiền */
        CallbackResponse response = buildDefaultResponse();
        response.setAmount(paymentVNPay.getAmount());
        response.setInvoiceNo(paymentVNPay.getInvoiceNo());
        response.setPaymentTime(DateUtils.getDate(DateUtils.DD_MM_YYYY_HH_MM_SS));

        switch (queryOrderResponse.getResponseCode()) {
            case "00" -> setMessageAndStatusIfQuerySuccess(paymentVNPay, response, queryOrderResponse);

            case "91" -> {
                response.setMessage("Đơn hàng không tồn tại");
                response.setResponseCode("NOT_FOUND");
            }

            default -> {
                response.setMessage("Giao dịch thất bại");
                response.setResponseCode("FAIL");
            }
        }

        /*-- Cập nhật thông tin lịch sử thanh toán --*/
//        updatePaymentHistory(paymentHistory, response.getStatus());

        updatePaymentVNPay(paymentVNPay, response);

        /*-- Nếu thanh toán thất bại → hủy đơn hàng tương ứng --*/
        if (isPaymentFailed(response.getResponseCode())) {
            LOGGER.info("[PAYMENT][VNPAY][CALLBACK] Giao dịch thất bại (responseCode={}), tiến hành hủy đơn hàng", response.getResponseCode());
            cancelCartByPaymentId(paymentVNPay.getId());
        }

        return response;
    }

    private void setMessageAndStatusIfQuerySuccess(PaymentVNPay paymentVNPay, CallbackResponse response, VNPayQueryOrderResponse queryOrderResponse) {
        /* TransactionStatus
         * Trường này được set khi có thông tin IPN do VNPAY trả về (KH đã thanh toán thành công hoặc thất bại)
         * 00: thành công
         * 01: đã thanh toán
         * null thì là chưa có IPN tức là chưa thanh toán
         * còn lại sẽ là fail
         * Nếu cần thêm case thì xem tại trang chủ VNPAY */
        if (StringUtils.isNullOrEmpty(queryOrderResponse.getTransactionStatus())) {
            response.setMessage("Đơn hàng chưa được xử lý");
            response.setResponseCode("ORDER_NOT_BEEN_PROCESSED");
        } else if (StringUtils.isNotNullOrEmpty(paymentVNPay.getCallbackResultCode())) {
            response.setMessage("Đơn hàng đã được xử lý");
            response.setResponseCode("ORDER_PROCESSED");
        } else {
            switch (queryOrderResponse.getTransactionStatus()) {
                case "00" -> {
                    response.setMessage("Thanh toán thành công");
                    response.setResponseCode("SUCCESS");
                }
                case "01" -> {
                    response.setMessage("Đơn hàng đã được xử lý");
                    response.setResponseCode("ORDER_PROCESSED");
                }
                default -> {
                    response.setMessage("Thanh toán thất bại");
                    response.setResponseCode("FAILED");
                }
            }
        }

    }

    private void updatePaymentVNPay(PaymentVNPay paymentVNPay, CallbackResponse response) {
        paymentVNPay.setCallbackResultCode(response.getResponseCode());
        paymentVNPayRepository.save(paymentVNPay);
    }

    /**
     * Kiểm tra xem responseCode từ callback có phải là trạng thái thanh toán thất bại không.
     * Không bao gồm ORDER_NOT_BEEN_PROCESSED (VNPay chưa xử lý, IPN chưa đến).
     */
    private boolean isPaymentFailed(String responseCode) {
        return "FAILED".equals(responseCode) || "FAIL".equals(responseCode) || "NOT_FOUND".equals(responseCode);
    }

    /**
     * Hủy đơn hàng gắn với paymentVNPayId bằng cách tra cứu qua PaymentHistory.
     * Chỉ hủy nếu đơn hàng vẫn đang ở trạng thái PENDING.
     */
    private void cancelCartByPaymentId(String paymentVNPayId) {
        paymentHistoryRepository.findByPaymentId(paymentVNPayId).ifPresent(history -> {
            cartRepository.updateStatusById(history.getOrderHistoryId(), "CANCELLED");
            LOGGER.info("[PAYMENT] Đã hủy đơn hàng {} do thanh toán thất bại", history.getOrderHistoryId());
        });
    }

    private VNPayQueryOrderRequest buildRequestQueryOrder(PaymentVNPay paymentVNPay, String requestId, String orderInfo, String ipAddress, String createDate, String secureHash) {
        var queryOrderRequest = new VNPayQueryOrderRequest();
        queryOrderRequest.setRequestId(requestId);
        queryOrderRequest.setVersion(version);
        queryOrderRequest.setCommand("querydr");
        queryOrderRequest.setTmnCode(tmnCode);
        queryOrderRequest.setTxnRef(paymentVNPay.getInvoiceNo());
        queryOrderRequest.setOrderInfo(orderInfo);
        queryOrderRequest.setTransactionNo(paymentVNPay.getTransactionNo());
        queryOrderRequest.setTransactionDate(paymentVNPay.getTransactionCreateDate());
        queryOrderRequest.setCreateDate(createDate);
        queryOrderRequest.setIpAddress(ipAddress);
        queryOrderRequest.setSecureHash(secureHash);
        return queryOrderRequest;
    }

    private static CallbackResponse buildDefaultResponse() {
        CallbackResponse callbackResponse = new CallbackResponse();
        callbackResponse.setResponseCode("FAIL");
        callbackResponse.setMessage("Thanh toán thất bại");
        callbackResponse.setAmount(BigInteger.ZERO);
        return callbackResponse;
    }

    private PaymentVNPay buildPaymentVNPay(int amount, String expiredDate, String username, String invoiceNo, String orderInfo, String createDate, String ipAddress) {
        var paymentVNPay = new PaymentVNPay();
        paymentVNPay.setUsername(username);
        paymentVNPay.setTransactionCreateDate(createDate);
        paymentVNPay.setTransactionExpiredDate(expiredDate);
        paymentVNPay.setStatus("INIT SUCCESS");
        paymentVNPay.setCardType(VN_BANK);
        paymentVNPay.setInvoiceNo(invoiceNo);
        paymentVNPay.setDescription(orderInfo);
        paymentVNPay.setAmount(BigInteger.valueOf(amount));
        paymentVNPay.setIpAddress(ipAddress);
        return paymentVNPay;
    }

    private HashMap<String, String> addParamsToMap(int amount, String invoiceNo, String orderInfo, String createDate, String expireDate, String ipAddress) {
        var vnpayParams = new HashMap<String, String>();
        vnpayParams.put(VERSION_PARAM, version);
        vnpayParams.put(COMMAND_PARAM, command);
        vnpayParams.put(TMN_CODE_PARAM, tmnCode);
        vnpayParams.put(AMOUNT_PARAM, String.valueOf(amount * 100));
        vnpayParams.put(CURR_CODE_PARAM, VND);
//        vnpayParams.put(BANK_CODE_PARAM, "VNPAYQR");
        vnpayParams.put(TXN_REF_PARAM, invoiceNo);
        vnpayParams.put(ORDER_INFO_PARAM, orderInfo);
        vnpayParams.put(ORDER_TYPE_PARAM, OTHER);
        vnpayParams.put(LOCALE_PARAM, VN);
        vnpayParams.put(RETURN_URL_PARAM, callbackUrl);
        vnpayParams.put(IP_ADDR_PARAM, ipAddress);
        vnpayParams.put(CREATE_DATE_PARAM, createDate);
        vnpayParams.put(EXPIRE_DATE_PARAM, expireDate);
        return vnpayParams;
    }

    private static void encodeAndBuildQueryUrl(HashMap<String, String> vnpayParams, ArrayList<String> fieldNames, StringBuilder hashData, StringBuilder query) {
        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = vnpayParams.get(fieldName);
            if ((fieldValue != null) && (!fieldValue.isEmpty())) {
                hashData.append(fieldName);
                hashData.append("=");
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));

                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII));
                query.append("=");
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                if (itr.hasNext()) {
                    query.append("&");
                    hashData.append("&");
                }
            }
        }
    }

    private static HashMap<String, String> buildParamsToMap(HttpServletRequest request) {
        var fields = new HashMap<String, String>();

        for (var params = request.getParameterNames(); params.hasMoreElements(); ) {
            var fieldName = URLEncoder.encode(params.nextElement(), StandardCharsets.US_ASCII);
            var fieldValue = URLEncoder.encode(request.getParameter(fieldName), StandardCharsets.US_ASCII);
            if ((Objects.nonNull(fieldValue) && (!fieldValue.isEmpty()))) {
                fields.put(fieldName, fieldValue);
            }
        }

        fields.remove(SECURE_HASH_TYPE);
        fields.remove(SECURE_HASH_PARAM);
        return fields;
    }

    @Override
    public VNPayIPNResponse processIPN(Map<String, String> parametersMap, HttpServletRequest request) {
        LOGGER.info("[VNPAY][IPN] Starting Request Parameters: {}", parametersMap);

        var params = objectMapper.convertValue(parametersMap, VNPayParameters.class);
        LOGGER.info("[VNPAY][IPN] Convert To Object Parameters: {}", params);

        /* Vì secure hash trên ipn là đoạn mã encode ko có chứa secure hash nên sẽ remove key này */
        var mapEncode = buildParamsToMap(request);

        var signValue = HMacUtils.hashAllFields(mapEncode, hashSecret);
        LOGGER.info("[VNPAY][IPN] Build Secure Hash: {}", signValue);

        if (signValue.equals(params.getSecureHash())) {
            LOGGER.info("[VNPAY][IPN] Secure Hash Matched");

            /*  Kiểm tra xem có tồn tại đơn hàng này hay không */
            var paymentVNPay = paymentVNPayRepository.getByInvoiceNo(params.getTxnRef());
            LOGGER.info("[VNPAY][IPN] VNPay Order Payment: {}", paymentVNPay);

            if (Objects.isNull(paymentVNPay)) {
                return VNPayIPNResponse.builder().responseCode("01").responseMessage("Order not Found").build();
            }

            var amount = params.getAmount().divide(BigInteger.valueOf(100L));
            LOGGER.info("[VNPAY][IPN] Số tiền thanh toán: {}", amount);

            /* Kiểm tra xem có đúng số tiền thanh toán hay không */
            if (!Objects.equals(paymentVNPay.getAmount(), amount)) {
                return VNPayIPNResponse.builder().responseCode("04").responseMessage("Invalid Amount").build();
            }

//            String messageBot = "%s - %s\n Mã đơn hàng: %s\nSố tiền: " + amount;

            if ("INIT SUCCESS".equals(paymentVNPay.getStatus())) {
                if ("00".equals(params.getResponseCode())) {
                    paymentVNPay.setTransactionStatus(params.getResponseCode());
                    paymentVNPay.setStatus("SUCCESS");
                    paymentVNPay.setTransactionNo(params.getTransactionNo());
                    paymentVNPay.setCardType(params.getCardType());
                    paymentVNPay.setBankTranNo(params.getBankTranNo());
                    cartRepository.updateStatus(paymentVNPay.getUsername(), "PAYMENT SUCCESS");
//                    messageBot = String.format(messageBot, paymentVNPay.getUsername(), "Đã thanh toán thành công", params.getTxnRef());
//                    telegramProxy.sendMessage(messageBot);
                } else if ("24".equals(params.getResponseCode())) {
                    paymentVNPay.setTransactionStatus(params.getTransactionStatus());
                    paymentVNPay.setStatus("CANCEL");
                    cancelCartByPaymentId(paymentVNPay.getId());
                } else {
                    paymentVNPay.setTransactionStatus(params.getTransactionStatus());
                    paymentVNPay.setStatus("FAIL");
                    cancelCartByPaymentId(paymentVNPay.getId());
                }
                paymentVNPayRepository.save(paymentVNPay);
            } else {
                return VNPayIPNResponse.builder().responseCode("02").responseMessage("Order already confirmed").build();
            }
            return VNPayIPNResponse.builder().responseCode("00").responseMessage("Confirm Success").build();
        }
        return VNPayIPNResponse.builder().responseCode("97").responseMessage("Invalid Checksum").build();
    }

}


