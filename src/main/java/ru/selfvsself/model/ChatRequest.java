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
    private String content;
    private UUID requestId;
    private UUID userId;
    private boolean useMessageHistory = true;
    private boolean useLocalModel = true;
}
