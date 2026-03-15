package com.example.pcstore.configurations;

import com.example.pcstore.exception.BusinessException;
import io.minio.MinioClient;
import io.minio.messages.Bucket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class MinioConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(MinioConfiguration.class);

    @Value("${minio.url}")
    private String minioUrl;

    @Value("${minio.access-key}")
    private String accessKey;

    @Value("${minio.secret-key}")
    private String secretKey;

    @Bean
    public MinioClient minioClient() {
        try {
            MinioClient client = MinioClient.builder()
                    .endpoint(minioUrl)
                    .credentials(accessKey, secretKey)
                    .build();

            List<String> buckets = client.listBuckets().stream().map(Bucket::name).toList();
            LOGGER.info("[MINIO] Kết nối thành công: {}. Buckets: {}", minioUrl, buckets);

            return client;
        } catch (Exception e) {
            LOGGER.error("[MINIO] Kết nối không thành công đến server: {}. Exception: {}", minioUrl, e.getMessage());
            throw new BusinessException("Kết nối không thành công đến server Minio");
        }
    }

}
