package ru.selfvsself.home.telegram.assistant.bot.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.LongPollingBot;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.selfvsself.home.telegram.assistant.bot.service.TelegramService;

@Slf4j
@Configuration
public class BotInitializer {
    private final TelegramService telegramBot;

    public BotInitializer(TelegramService telegramBot) {
        this.telegramBot = telegramBot;
    }

    @EventListener({ContextRefreshedEvent.class})
    public void init() {
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot((LongPollingBot) telegramBot);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }
}
