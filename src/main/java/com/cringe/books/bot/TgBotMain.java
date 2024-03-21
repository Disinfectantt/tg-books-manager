package com.cringe.books.bot;

import com.cringe.books.provider.CustomAuthenticationProvider;
import com.cringe.books.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class TgBotMain extends TelegramLongPollingBot {
    private static final Logger logger = LoggerFactory.getLogger(CustomAuthenticationProvider.class);
    private final BotConfig botConfig;
    private final UserService userService;

    public TgBotMain(BotConfig botConfig, UserService userService) {
        super(new DefaultBotOptions(), botConfig.getBotToken());
        this.botConfig = botConfig;
        this.userService = userService;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            Long userId = update.getCallbackQuery().getFrom().getId();
            if (update.getMessage().hasText()) {
                String msgTxt = update.getMessage().getText();
                if (msgTxt.equalsIgnoreCase("/start")) {
                    userService.addUser(userId);
                }
                sendMsg(update.getMessage().getChatId(),
                        "Welcome! Send me a photo of your book");
            } else if (update.getMessage().hasPhoto()) {
                if (userService.isWhitelist() && !userService.isInWhitelist(userId)) {
                    return;
                }
                //TODO handle image

            }
        }
    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    private void sendMsg(Long chatId, String msg) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(msg);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            logger.info(e.getMessage());
        }
    }

}
