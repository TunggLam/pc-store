package com.example.pcstore.service;

import com.example.pcstore.model.request.VNPayCallbackRequest;
import com.example.pcstore.model.response.CallbackResponse;
import com.example.pcstore.model.response.VNPayIPNResponse;
import com.example.pcstore.model.response.VNPayInitOrderResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

public interface PaymentService {

    VNPayInitOrderResponse initOrder();

    CallbackResponse callback(VNPayCallbackRequest request);

    VNPayIPNResponse processIPN(Map<String, String> parametersMap, HttpServletRequest request);
}

