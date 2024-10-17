package ru.selfvsself.home_telegram_assistant_bot.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.selfvsself.model.ChatRequest;

@Service
public class KafkaProducerService {

    private final KafkaTemplate<String, ChatRequest> kafkaTemplate;
    @Value("${kafka.topic.request}")
    private String requestTopic;

    public KafkaProducerService(KafkaTemplate<String, ChatRequest> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(ChatRequest chatRequest) {
        kafkaTemplate.send(requestTopic, chatRequest);
    }
}
