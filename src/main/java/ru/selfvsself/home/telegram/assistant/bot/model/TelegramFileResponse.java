package ru.selfvsself.home.telegram.assistant.bot.model;

import lombok.Data;

import java.util.Optional;

@Data
public class TelegramFileResponse {
    private boolean ok;
    private Result result;

    public String getFileUrl() {
        return Optional.ofNullable(result)
                .map(Result::getFile_path)
                .orElse("");
    }

    @Data
    private static class Result {
        private String file_id;
        private String file_unique_id;
        private int file_size;
        private String file_path;
    }
}
