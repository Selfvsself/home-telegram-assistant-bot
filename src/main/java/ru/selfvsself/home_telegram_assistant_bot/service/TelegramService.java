package ru.selfvsself.home_telegram_assistant_bot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.selfvsself.home_telegram_assistant_bot.model.database.User;
import ru.selfvsself.home_telegram_assistant_bot.service.database.UserService;
import ru.selfvsself.model.ChatRequest;
import ru.selfvsself.model.ChatResponse;

import java.util.Optional;
import java.util.UUID;

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
            String userName = Optional.ofNullable(update.getMessage().getFrom())
                    .map(from -> from.getUserName()).orElse("unknown");
            log.info("Receive message chatId: {}, userName: {}, text: {}",
                    chatId, userName, update.getMessage().getText());
            if (update.getMessage().hasText()) {
                String messageText = update.getMessage().getText();
                if (messageText.equals("/start")) {
                    messageText = "Привет";
                }
                kafkaProducerService.sendTextMessage(chatId, userName, messageText);
            }
        }
    }

    public void processResponse(long chatId, String model, String content) {
        String text = String.format("%s\n\n%s", model, content);
        for (int i = 0; i < text.length(); i += maxMessageLength) {
            sendMessage(chatId, text.substring(i, Math.min(text.length(), i + maxMessageLength)));
        }
    }

    private void sendMessage(long chatId, String text) {
        try {
            SendMessage message = new SendMessage();
            message.setChatId(chatId);
            message.setText(text);
            log.info("to chat '{}' send message '{}'", chatId, text);
            execute(message);
        } catch (Exception e) {
            log.error("Could not send a message to chat id {}, message body: {}",
                    chatId,
                    text);
            log.error(e.getMessage());
        }
    }
}
