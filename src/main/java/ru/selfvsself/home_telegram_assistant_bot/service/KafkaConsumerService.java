package ru.selfvsself.home_telegram_assistant_bot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.selfvsself.home_telegram_assistant_bot.model.database.User;
import ru.selfvsself.home_telegram_assistant_bot.service.database.UserService;
import ru.selfvsself.model.ChatResponse;
import ru.selfvsself.model.ResponseType;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class KafkaConsumerService {

    private final TelegramService telegramService;
    private final UserService userService;

    public KafkaConsumerService(TelegramService telegramService, UserService userService) {
        this.telegramService = telegramService;
        this.userService = userService;
    }

    @KafkaListener(topics = "${kafka.topic.response}", groupId = "${kafka.group}", containerFactory = "textRequestKafkaListenerContainerFactory")
    public void responseProcessing(ChatResponse response) {
        log.info("Response: {}", response);
        if (response.getRequestId() == null) {
            throw new IllegalArgumentException("Request id is null, response is " + response);
        }
        if (response.getParticipant() == null) {
            throw new IllegalArgumentException("Participant is null, response is " + response);
        }
        Long chatId = response.getParticipant().getChatId();
        if (chatId == null) {
            chatId = userService.findChatIdByUserId(response.getParticipant().getUserId());
        }
        if (!StringUtils.hasLength(response.getModel())) {
            log.error("Model is empty, response is {}", response);
            response.setModel("Something went wrong");
        }
        if (!StringUtils.hasLength(response.getContent())) {
            log.error("Content is empty, response is {}", response);
            response.setContent("Unfortunately, it’s not possible to receive a response at the moment. Please try again later.");
        }
        if (!ResponseType.SUCCESS.equals(response.getType())) {
            log.info("ResponseType is not SUCCESS, current response type is {}, requestId is {}",
                    response.getType(),
                    response.getRequestId());
            response.setModel("Something went wrong");
            response.setContent("Unfortunately, it’s not possible to receive a response at the moment. Please try again later.");
        }
        String model = response.getModel();
        String content = response.getContent();
        telegramService.processResponse(chatId, model, content);
    }
}
