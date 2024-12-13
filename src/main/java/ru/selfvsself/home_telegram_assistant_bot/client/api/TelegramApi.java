package ru.selfvsself.home_telegram_assistant_bot.client.api;

import java.io.InputStream;
import java.util.function.Consumer;

public interface TelegramApi {
    String getFileUrl(String fileId);

    void processFile(long chatId, String fileUrl, Consumer<InputStream> putFile);
}
