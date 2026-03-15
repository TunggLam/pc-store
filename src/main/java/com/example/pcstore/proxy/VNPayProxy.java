package com.example.pcstore.proxy;

import com.example.pcstore.logging.LoggingFactory;
import com.example.pcstore.model.proxy.request.VNPayQueryOrderRequest;
import com.example.pcstore.model.proxy.response.VNPayQueryOrderResponse;
import com.example.pcstore.utils.JsonUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@RequiredArgsConstructor
public class VNPayProxy extends BaseProxy {

    private static final Logger LOGGER = LoggingFactory.getLogger(VNPayProxy.class);

    @Value("${vnpay.domain}")
    private String domain;

    public VNPayQueryOrderResponse queryOrder(VNPayQueryOrderRequest request) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(domain).path("/merchant_webapi/api/transaction").toUriString();
            LOGGER.info("[PROXY][VNPAY][QUERY ORDER] Url: {}", url);

            String payload = JsonUtils.toJson(request);
            LOGGER.info("[PROXY][VNPAY][QUERY ORDER] Payload: {}", payload);

            VNPayQueryOrderResponse response = this.post(url, integer -> initHeaders(), payload, VNPayQueryOrderResponse.class);
            LOGGER.info("[PROXY][VNPAY][QUERY ORDER] Response: {}", response);

            return response;
        } catch (Exception e) {
            LOGGER.info("[PROXY][VNPAY][QUERY ORDER] Exception: {}", e.getMessage());
            return null;
        }
    }
}

