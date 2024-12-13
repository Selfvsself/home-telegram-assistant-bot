package ru.selfvsself.home_telegram_assistant_bot.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import ru.selfvsself.home_telegram_assistant_bot.client.api.TelegramApi;
import ru.selfvsself.home_telegram_assistant_bot.model.TelegramFileResponse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.function.Consumer;

@Slf4j
@Service
public class TelegramClient implements TelegramApi {

    private final WebClient webClient;

    @Value("${bot.token}")
    private String botToken;

    public TelegramClient(WebClient telegramApiClient) {
        this.webClient = telegramApiClient;
    }

    @Override
    public void processFile(long chatId, String fileUrl, Consumer<InputStream> putFile) {
        if (StringUtils.hasLength(fileUrl)) {
            var bytes = getFileByUrl(fileUrl);
            try (InputStream inputStream = new ByteArrayInputStream(bytes)) {
                putFile.accept(inputStream);
            } catch (IOException e) {
                log.error("Error read audio file chatId: {}, fileId: {}, message: {}",
                        chatId, fileUrl, e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getFileUrl(String fileId) {
        var response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .pathSegment(String.format("bot%s", botToken))
                        .path("getFile")
                        .queryParam("file_id", fileId)
                        .build())
                .retrieve()
                .bodyToMono(TelegramFileResponse.class)
                .block();
        return Optional.ofNullable(response)
                .map(TelegramFileResponse::getFileUrl)
                .orElse("");
    }


    private byte[] getFileByUrl(String fileUrl) {
        var response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("file")
                        .pathSegment(String.format("bot%s", botToken))
                        .path(fileUrl)
                        .build())
                .retrieve()
                .bodyToMono(byte[].class)
                .block();
        return response;
    }
}
