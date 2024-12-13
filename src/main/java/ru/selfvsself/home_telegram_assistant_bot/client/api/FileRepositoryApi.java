package ru.selfvsself.home_telegram_assistant_bot.client.api;

import java.io.InputStream;

public interface FileRepositoryApi {

    boolean bucketExists(String bucketName);

    void makeBucket(String bucketName);

    boolean putFile(String bucketName, String fileName, InputStream inputStream, String contentType);

    InputStream getFile(String bucketName, String fileName);

}
