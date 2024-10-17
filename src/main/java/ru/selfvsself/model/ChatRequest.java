package ru.selfvsself.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequest {
    private Long chatId;
    private String userName;
    private String content;
    private UUID requestId;
    private boolean useMessageHistory = true;
    private boolean useLocalModel = true;
}
