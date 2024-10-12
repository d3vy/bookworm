package com.project.shoppingwithbookwormbot.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class BotConfig {

    @Value("${bot.name}")
    String name;
    @Value("${bot.token}")
    String token;
    @Value("${bot.owner-id}")
    Long ownerChatId;
    @Value("${bot.owner2-id}")
    Long owner2ChatId;
    @Value("${bot.owner3-id}")
    Long owner3ChatId;



}
