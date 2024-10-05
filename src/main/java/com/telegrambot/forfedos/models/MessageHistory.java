package com.telegrambot.forfedos.models;

import lombok.Data;
import lombok.Getter;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

@Getter
@Data
public class MessageHistory {
    private final Integer messageId;
    private final String text;
    private final InlineKeyboardMarkup replyMarkup;
}