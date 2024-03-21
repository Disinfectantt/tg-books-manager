package com.cringe.books.bot;

import com.cringe.books.provider.CustomAuthenticationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Component
public class InitBot {
    private final TgBotMain tgBotMain;
    private static final Logger logger = LoggerFactory.getLogger(CustomAuthenticationProvider.class);

    public InitBot(TgBotMain tgBotMain){
        this.tgBotMain = tgBotMain;
    }

    @EventListener({ContextRefreshedEvent.class})
    public void init() {
        try{
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(tgBotMain);
        }catch (TelegramApiException e){
            logger.error(e.getMessage());
        }

    }

}
