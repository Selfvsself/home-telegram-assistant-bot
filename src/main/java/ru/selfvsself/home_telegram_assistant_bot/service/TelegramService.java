package ru.selfvsself.home_telegram_assistant_bot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.selfvsself.model.ChatRequest;
import ru.selfvsself.model.ChatResponse;

@Slf4j
@Service
public class TelegramService extends TelegramLongPollingBot {

    @Value("${bot.name}")
    private String botName;
    @Value("${bot.token}")
    private String botToken;
    @Value("${bot.max-message-length}")
    private Integer maxMessageLength;

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            long chatId = update.getMessage().getChatId();
            String userName = update.getMessage().getFrom().getUserName();
            if (update.getMessage().hasText()) {
                String messageText = update.getMessage().getText();
                if (messageText.equals("/start")) {
                    messageText = "Привет";
                }
                ChatRequest chatRequest = ChatRequest.builder()
                        .chatId(chatId)
                        .userName(userName)
                        .content(messageText)
                        .useMessageHistory(true)
                        .useLocalModel(false)
                        .build();
                kafkaProducerService.sendMessage(chatRequest);
            }
        }
    }

    public void processResponse(ChatResponse chatResponse) {
        long chatId = chatResponse.getChatId();
        String userName = chatResponse.getUserName();
        String text = String.format("Model: %s\n\n%s",
                chatResponse.getModel(),
                chatResponse.getContent());
        for (int i = 0; i < text.length(); i += maxMessageLength) {
            sendMessage(chatId, userName, text.substring(i, Math.min(text.length(), i + maxMessageLength)));
        }
    }

    private void sendMessage(long chatId, String userName, String text) {
        try {
            SendMessage message = new SendMessage();
            message.setChatId(chatId);
            message.setText(text);
            log.info("to '{}' chat '{}' send message '{}'", userName, chatId, text);
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Could not send a message to chat id {}, message body: {}",
                    chatId,
                    text);
            log.error(e.getMessage());
        }
    }
}
