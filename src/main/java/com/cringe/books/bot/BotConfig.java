package com.cringe.books.bot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class BotConfig {
    private final String botName = "booksBot";
    @Value("${botToken}")
    private String botToken;

    public String getBotToken() {
        return botToken;
    }

    public String getBotName() {
        return botName;
    }
}
