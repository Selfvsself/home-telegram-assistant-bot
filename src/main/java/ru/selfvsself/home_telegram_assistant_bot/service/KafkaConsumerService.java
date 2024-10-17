package ru.selfvsself.home_telegram_assistant_bot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.selfvsself.model.ChatResponse;

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
        telegramService.processResponse(response);
    }
}
