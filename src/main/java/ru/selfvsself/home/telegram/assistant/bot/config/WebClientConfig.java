package ru.selfvsself.home.telegram.assistant.bot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${telegram-api.base-url}")
    private String telegramApiUrl;

    @Value("${spring.codec.max-in-memory-size-mb}")
    private Integer maxInMemorySize;

    @Bean
    public WebClient telegramApiClient() {
        return WebClient.builder()
                .baseUrl(telegramApiUrl)
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(codecs -> codecs
                                .defaultCodecs().maxInMemorySize(250 * 1024 * 1024))
                        .build())
                .build();
    }
}
