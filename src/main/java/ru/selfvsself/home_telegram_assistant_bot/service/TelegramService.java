package ru.selfvsself.home_telegram_assistant_bot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.*;

import java.util.Optional;

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

    @Autowired
    private FileRepositoryService fileRepositoryService;

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
                    .map(User::getUserName).orElse("unknown");
            if (update.getMessage() != null) {
                Message message = update.getMessage();
                log.info("Receive message chatId: {}, userName: {}, message: {}",
                        chatId, userName, message.toString());
                if (message.hasVoice()) {
                    Voice voice = message.getVoice();
                    processVoiceMessage(chatId, userName, voice.getFileId(), voice.getDuration());
                } else if (update.getMessage().hasAudio()) {
                    Audio audio = message.getAudio();
                    processAudioMessage(chatId, userName, audio.getFileId(), audio.getDuration());
                } else if (update.getMessage().hasPhoto()) {
                    var photo = message.getPhoto().stream()
                            .min((i, y) -> y.getFileSize().compareTo(i.getFileSize()))
                            .orElse(null);
                    if (photo != null) {
                        processPhotoMessage(chatId, userName, photo.getFileId());
                    } else {
                        log.error("Photo is null chatId: {}, userName: {}",
                                chatId, userName);
                    }
                } else if (update.getMessage().hasVideo()) {
                    Video video = message.getVideo();
                    processVideoMessage(chatId, userName, video.getFileId(), video.getDuration());
                } else if (message.hasText()) {
                    processTextMessage(chatId, userName, message.getText());
                }
            }
        }
    }

    private void processTextMessage(long chatId, String userName, String text) {
        String messageText = text;
        log.info("Receive text message chatId: {}, userName: {}, text: {}",
                chatId, userName, messageText);
        if (messageText.equals("/start")) {
            messageText = "Привет";
        }
        messageRequestProcessing(chatId, userName, messageText);
    }

    private void processVoiceMessage(long chatId, String userName, String voiceId, Integer duration) {
        if (duration == null) {
            log.error("Voice duration is null chatId: {}, userName: {}, voiceId: {}",
                    chatId, userName, voiceId);
        } else if (duration > 600) {
            sendMessage(chatId, "Слишком длинное голосовое сообщение");
        } else {
            log.info("Receive voice message chatId: {}, userName: {}, voiceId: {}",
                    chatId, userName, voiceId);
            String fileName = fileRepositoryService.downloadFile(chatId, voiceId);
            log.info("Downloaded file: {}, chatId: {}", fileName, chatId);
        }
    }

    private void processAudioMessage(long chatId, String userName, String voiceId, Integer duration) {
        if (duration == null) {
            log.error("Audio duration is null chatId: {}, userName: {}, voiceId: {}",
                    chatId, userName, voiceId);
        } else if (duration > 600) {
            sendMessage(chatId, "Слишком длинное аудио сообщение");
        } else {
            log.info("Receive audio message chatId: {}, userName: {}, voiceId: {}",
                    chatId, userName, voiceId);
            String fileName = fileRepositoryService.downloadFile(chatId, voiceId);
            log.info("Downloaded file: {}, chatId: {}", fileName, chatId);
        }
    }

    private void processPhotoMessage(long chatId, String userName, String voiceId) {
        log.info("Receive photo message chatId: {}, userName: {}, voiceId: {}",
                chatId, userName, voiceId);
        String fileName = fileRepositoryService.downloadFile(chatId, voiceId);
        log.info("Downloaded file: {}, chatId: {}", fileName, chatId);
    }

    private void processVideoMessage(long chatId, String userName, String voiceId, Integer duration) {
        if (duration == null) {
            log.error("Video duration is null chatId: {}, userName: {}, voiceId: {}",
                    chatId, userName, voiceId);
        } else if (duration > 600) {
            sendMessage(chatId, "Слишком длинное видео");
        } else {
            log.info("Receive video message chatId: {}, userName: {}, voiceId: {}",
                    chatId, userName, voiceId);
            String fileName = fileRepositoryService.downloadFile(chatId, voiceId);
            log.info("Downloaded file: {}, chatId: {}", fileName, chatId);
        }
    }

    public void messageRequestProcessing(long chatId, String userName, String text) {
        try {
            kafkaProducerService.sendTextMessage(chatId, userName, text);
        } catch (Exception e) {
            log.error(e.getMessage());
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
