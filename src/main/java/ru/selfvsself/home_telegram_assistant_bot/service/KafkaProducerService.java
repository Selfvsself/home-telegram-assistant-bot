package ru.selfvsself.home_telegram_assistant_bot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.selfvsself.home_telegram_assistant_bot.model.database.User;
import ru.selfvsself.home_telegram_assistant_bot.service.database.UserService;
import ru.selfvsself.model.ChatRequest;
import ru.selfvsself.model.Content;
import ru.selfvsself.model.Participant;

import java.util.UUID;

@Slf4j
@Service
public class KafkaProducerService {

    private final KafkaTemplate<String, ChatRequest> kafkaTemplate;

    private final UserService userService;
    @Value("${kafka.topic.request}")
    private String requestTopic;

    public KafkaProducerService(KafkaTemplate<String, ChatRequest> kafkaTemplate, UserService userService) {
        this.kafkaTemplate = kafkaTemplate;
        this.userService = userService;
    }

    public void sendTextMessage(long chatId, String userName, String text) {
        if (!StringUtils.hasLength(userName)) {
            log.error("User name is empty, chatId is {}", chatId);
            throw new IllegalArgumentException("User name is empty, chatId is " + chatId);
        }
        if (!StringUtils.hasLength(text)) {
            log.error("Text is empty, chatId is {}", chatId);
            throw new IllegalArgumentException("Text is empty, chatId is " + chatId);
        }
        User user = userService.addUserIfNotExists(chatId, userName);
        ChatRequest chatRequest = ChatRequest.builder()
                .requestId(UUID.randomUUID())
                .participant(new Participant(user.getId(), chatId))
                .content(Content.builder().text(text).build())
                .useMessageHistory(true)
                .useLocalModel(false)
                .build();
        kafkaTemplate.send(requestTopic, user.getId().toString(), chatRequest);
    }
}
