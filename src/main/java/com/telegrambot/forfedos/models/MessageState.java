package com.telegrambot.forfedos.models;

import lombok.Data;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;


@Data
public class MessageState {
    private final String text;
    private final InlineKeyboardMarkup replyMarkup;

}
