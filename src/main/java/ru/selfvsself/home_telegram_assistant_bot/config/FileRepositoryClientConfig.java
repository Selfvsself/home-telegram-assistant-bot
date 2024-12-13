package ru.selfvsself.home_telegram_assistant_bot.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class FileRepositoryClientConfig {

    @Value("${file-repository.base-url}")
    private String baseUrl;

    @Value("${file-repository.access-key}")
    private String accessKey;

    @Value("${file-repository.secret-key}")
    private String secretKey;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(baseUrl)
                .credentials(accessKey, secretKey)
                .build();
    }
}
