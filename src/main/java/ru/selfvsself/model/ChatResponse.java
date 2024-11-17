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
public class ChatResponse {
    private String model;
    private String content;
    private UUID requestId;
    private Participant participant;
    private ResponseType type;
}
