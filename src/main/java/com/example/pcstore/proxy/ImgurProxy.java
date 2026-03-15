package com.example.pcstore.proxy;
import com.example.pcstore.model.proxy.response.ImgurUploadResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;
@Component
@RequiredArgsConstructor
public class ImgurProxy extends BaseProxy{

    private static final String CLIENT_ID = "Client-Id %s";

    @Value("${imgur.client-id}")
    private String clientId;

    @Value("${imgur.upload-url}")
    private String urlUpload;

    public ImgurUploadResponse upload(Map<String, Object> payload) {
        try{
            String url = UriComponentsBuilder.fromHttpUrl(urlUpload).toUriString();

            String client = String.format(CLIENT_ID, clientId);

            ImgurUploadResponse response = this.post(url, integer -> initHeaders(client), payload, ImgurUploadResponse.class);

            return response;
        } catch (Exception e) {
            return null;
        }

    }
}