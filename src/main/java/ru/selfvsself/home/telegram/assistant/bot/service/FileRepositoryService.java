package ru.selfvsself.home.telegram.assistant.bot.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.selfvsself.home.telegram.assistant.bot.client.api.FileRepositoryApi;
import ru.selfvsself.home.telegram.assistant.bot.client.api.TelegramApi;

@Slf4j
@Service
public class FileRepositoryService {

    private final TelegramApi telegramApi;

    private final FileRepositoryApi fileRepositoryApi;

    @Value("${file-repository.bucket-name}")
    private String bucketName;

    public FileRepositoryService(TelegramApi telegramApi, FileRepositoryApi fileRepositoryApi) {
        this.telegramApi = telegramApi;
        this.fileRepositoryApi = fileRepositoryApi;
    }

    @PostConstruct
    public void createBucket() {
        createBucketIfNotExists();
    }

    private void createBucketIfNotExists() {
        if (!fileRepositoryApi.bucketExists(bucketName)) {
            fileRepositoryApi.makeBucket(bucketName);
        }
    }

    public String downloadFile(long chatId, String fileId) {
        String fileUrl = telegramApi.getFileUrl(fileId);
        String fileName = String.format("%s/%s", chatId, fileUrl);
        telegramApi.processFile(chatId, fileUrl, inputStream -> fileRepositoryApi
                .putFile(bucketName, fileName, inputStream, "application/octet-stream"));
        return String.format("%s/%s", bucketName, fileName);
    }
}
