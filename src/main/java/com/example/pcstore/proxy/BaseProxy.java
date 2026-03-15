package com.example.pcstore.proxy;

import com.example.pcstore.logging.LoggingFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.Map;
import java.util.function.Function;

@Component
public class BaseProxy {

    private static final Logger LOGGER = LoggingFactory.getLogger(BaseProxy.class);

    @Autowired
    private RestTemplate restTemplate;

    protected HttpHeaders initHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    protected HttpHeaders initHeaders(String clientId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set("Authorization", clientId);
        return headers;
    }

    protected <R> R post(String url, Function<Integer, HttpHeaders> headersFunction, Map<String, Object> payload, Class<R> rClass) {
        return post(url, headersFunction, payload, 0, rClass);
    }

    protected <R> R post(String url, Function<Integer, HttpHeaders> headersFunction, String payload, Class<R> rClass) {
        return post(url, headersFunction, payload, 0, rClass);
    }

    protected <R> R get(String url, Function<Integer, HttpHeaders> headersFunction, Class<R> rClass) {
        return get(url, headersFunction, 0, rClass);
    }

    private <R> R get(String url, Function<Integer, HttpHeaders> headersFunction, int i, Class<R> rClass) {
        try {
            HttpHeaders headers = headersFunction.apply(i);
            LOGGER.info("[PROXY][GET] Headers: {}", headers);
            return this.get(url, headers, rClass);
        } catch (HttpClientErrorException e) {
            LOGGER.info("[PROXY][GET] HTTP Client Exception: {}", e.getMessage());
            if (i < 3 && e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                return this.get(url, headersFunction, i + 1, rClass);
            } else {
                throw e;
            }
        }
    }

    private <R> R post(String url, Function<Integer, HttpHeaders> headersFunction, Map<String, Object> payload, int i, Class<R> rClass) {
        try {
            HttpHeaders headers = headersFunction.apply(i);
            LOGGER.info("[PROXY][POST] Headers: {}", headers);

            MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
            for (Map.Entry<String, Object> entry : payload.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                if (value instanceof File file) {
                    map.add(key, new FileSystemResource(file));
                } else {
                    map.add(key, value);
                }
            }
            return this.post(url, headers, map, rClass);
        } catch (HttpClientErrorException e) {
            LOGGER.info("[PROXY][POST] HTTP Client Exception: {}", e.getMessage());
            if (i < 3 && e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                return this.post(url, headersFunction, payload, i + 1, rClass);
            } else {
                throw e;
            }
        }
    }

    private <R> R post(String url, Function<Integer, HttpHeaders> headersFunction, String payload, int i, Class<R> rClass) {
        try {
            HttpHeaders headers = headersFunction.apply(i);
            LOGGER.info("[PROXY][POST] Headers: {}", headers);

            return this.post(url, headers, payload, rClass);
        } catch (HttpClientErrorException e) {
            LOGGER.info("[PROXY][POST] HTTP Client Exception: {}", e.getMessage());
            if (i < 3 && e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                return this.post(url, headersFunction, payload, i + 1, rClass);
            } else {
                throw e;
            }
        }
    }

    private <R> R get(String url, HttpHeaders headers, Class<R> rClass) {
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<R> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, rClass);
        return responseEntity.getBody();
    }

    private <R> R post(String url, HttpHeaders headers, MultiValueMap<String, Object> map, Class<R> rClass) {
        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(map, headers);
        ResponseEntity<R> responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, rClass);
        return responseEntity.getBody();
    }

    private <R> R post(String url, HttpHeaders headers, String payload, Class<R> rClass) {
        HttpEntity<String> entity = new HttpEntity<>(payload, headers);
        ResponseEntity<R> responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, rClass);
        return responseEntity.getBody();
    }

}
