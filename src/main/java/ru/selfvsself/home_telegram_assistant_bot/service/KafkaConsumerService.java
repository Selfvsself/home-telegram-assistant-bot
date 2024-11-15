package ru.selfvsself.home_telegram_assistant_bot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.selfvsself.model.ChatResponse;
import ru.selfvsself.model.ResponseType;

@Slf4j
@Service
public class KafkaConsumerService {

    private final TelegramService telegramService;

    public KafkaConsumerService(TelegramService telegramService) {
        this.telegramService = telegramService;
    }

    @KafkaListener(topics = "${kafka.topic.response}", groupId = "${kafka.group}", containerFactory = "textRequestKafkaListenerContainerFactory")
    public void responseProcessing(ChatResponse response) {
        log.info("Response: {}", response);
        if (response.getUserId() == null) {
            log.error("UserId is null, requestId is {}", response.getRequestId());
            return;
        }
        if (!ResponseType.SUCCESS.equals(response.getType())) {
            log.info("Model is null, requestId is {}", response.getRequestId());
            response.setModel("Something went wrong");
            response.setContent("Unfortunately, it’s not possible to receive a response at the moment. Please try again later.");
        }
        telegramService.processResponse(response);
    }
}
