package ru.selfvsself.home.telegram.assistant.bot.client.api;

import java.io.InputStream;
import java.util.function.Consumer;

public interface TelegramApi {
    String getFileUrl(String fileId);

    void processFile(long chatId, String fileUrl, Consumer<InputStream> putFile);
}
