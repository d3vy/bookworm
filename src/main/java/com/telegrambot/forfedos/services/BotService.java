package com.telegrambot.forfedos.services;

import com.telegrambot.forfedos.config.BotConfig;
import com.telegrambot.forfedos.models.Customer;
import com.telegrambot.forfedos.models.Document;
import com.telegrambot.forfedos.models.PricedItem;
import com.telegrambot.forfedos.repositories.*;
import com.vdurmont.emoji.EmojiParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.*;

@RequiredArgsConstructor
@Component
@Slf4j
public class BotService extends TelegramLongPollingBot {

    private final BotConfig botConfig;
    private final CustomerRepository customerRepository;
    private final CategoryRepository categoryRepository;
    private final BankServiceRepository bankServiceRepository;
    private final DigitalServiceRepository digitalServiceRepository;
    private final DocumentsRepository documentsRepository;
    private final FinancialServiceRepository financialServiceRepository;
    private final OtherServiceRepository otherServiceRepository;


    @Value("${catalogue.count-of-categories}")
    private Integer countOfCategories;

    @Value("${catalogue.count-of-documents}")
    private Integer countOfDocuments;

    @Value("${catalogue.count-of-financial-services}")
    private Integer countOfFinancialServices;


    @Override
    public String getBotUsername() {
        return botConfig.getName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String message = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();

            if (message.contains("/send") && Objects.equals(botConfig.getOwnerChatId(), chatId)) {
                var textToSend = EmojiParser.parseToUnicode(message.substring(message.indexOf(" ")));
                var customers = customerRepository.findAll();
                for (Customer customer : customers) {
                    prepareAndSendMessage(customer.getChatId(), textToSend);
                }
            } else {
                switch (message) {
                    case "/start":
                        registerUser(update.getMessage());
                        startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                        getProductsCatalogue(chatId);
                        break;
                }
            }
        } else if (update.hasCallbackQuery()) {
            CallbackQuery callBackQuery = update.getCallbackQuery();
            String callBackData = callBackQuery.getData();
            Integer messageId = callBackQuery.getMessage().getMessageId();
            Long chatId = callBackQuery.getMessage().getChatId();
            Map<String, Integer> documents = findDocuments();
            switch (callBackData) {
                case "Документы":
                    showDocumentsCatalogue(chatId, messageId);
                    log.info(callBackQuery.toString());
                    break;
                case "Финансовые услуги":
                    ShowFinancialServiceCatalogue(chatId, messageId);
                    break;
                case "Банковские услуги":
                    showBankServicesCatalogue(chatId, messageId);
                    break;
                case "Цифровые услуги":
                    showDigitalServicesCatalogue(chatId, messageId);
                    break;
                case "Прочее":
                    showOtherOptionsCatalogue(chatId, messageId);
                    break;
                default:
                    showPrice(chatId, messageId, callBackData);
                    break;
            }
        }
    }

    //Нужно сделать так, чтобы этот метод был един для всех документов, поэтому нужно создать общий репозиторий, а в методе проверять какой это именно документ.

    private void showPrice(Long chatId, Integer messageId, String callbackData) {
        EditMessageText message = new EditMessageText();
        message.setChatId(chatId.toString());
        message.setMessageId(messageId);

        Optional<? extends PricedItem> itemInfo = Optional.empty();

        // Поиск данных в репозиториях
        if (Arrays.asList("СТС", "ПТС", "Водительское удостоверение", "ГИМС",
                        "Номерные знаки", "Подбор двойника", "Военный билет",
                        "Паспорт", "Аттестат", "Дипломы", "Временное гражданство", "СНИЛС")
                .contains(callbackData)) {
            itemInfo = documentsRepository.findByName(callbackData);
        } else if (Arrays.asList("Чистые карты", "Восстановление").contains(callbackData)) {
            itemInfo = financialServiceRepository.findByName(callbackData);
        }

        // Обработка данных
        itemInfo.ifPresentOrElse(
                item -> message.setText("Цена на %s: %d\n".formatted(item.getName().toLowerCase(), item.getPrice()) +
                        "Договориться о приобретении: %s\n".formatted("@ссылка на аккаунт") +
                        "За каждого приведенного друга даем 10000 рублей!"),
                () -> {
                    message.setText("Документ не найден");
                    log.error("Элемент с названием {} не найден", callbackData);
                }
        );

        // Отправка сообщения
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Ошибка при отправке цены пользователю: {}", e.getMessage());
        }
    }

    private Map<String, Integer> findDocuments() {
        Map<String, Integer> documents = new HashMap<>();
        for (int i = 1; i <= countOfDocuments; i++) {
            var document = documentsRepository.findById(i);
            if (document.isPresent()) {
                documents.put(document.get().getName(), i);
            } else {
                log.error("Документ не найден");
            }
        }
        return documents;
    }

    //Метод для отправки Message пользователю.
    private void executeMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Ошибка при отправке сообщения пользователю: {}", e.getMessage());
        }
    }

    //Метод, отвечающий за отправку определенного сообщения от бота пользователю.
    private void sendMessage(long chatId, String messageToSend) {
        //Создание сообщения
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(messageToSend);

        //Отправка сообщения пользователю.
        executeMessage(message);
    }

    //Метод, создающий сообщение для дальнейше отправки пользователю.
    private void prepareAndSendMessage(long chatId, String messageToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(messageToSend);
        executeMessage(message);
    }

    //Метод, отвечающий за добавление информации о пользователе в базу данных при нажатии на кнопку start.
    private void registerUser(Message message) {
        if (customerRepository.findById(message.getChatId()).isEmpty()) {
            var chatId = message.getChatId();
            var chat = message.getChat();
            Customer customer = new Customer();

            customer.setChatId(chatId);
            customer.setName(chat.getFirstName());

            customerRepository.save(customer);

            log.info("Customer {} has been saved to the database", customer);
        }
    }

    //Обработка метода, отвечающего за команду start.
    private void startCommandReceived(Long chatId, String name) {
        String answer = EmojiParser.parseToUnicode("Здравствуйте, " + name + "!");

        sendMessage(chatId, answer);
        log.info("Ответили пользователю, который нажал /start");
    }

    private void getProductsCatalogue(Long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Ниже приведен каталог услуг, которые мы предоставляем.");
        log.info("Создали сообщение");
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = getMenu();
        inlineKeyboardMarkup.setKeyboard(rowsInline);
        message.setReplyMarkup(inlineKeyboardMarkup);
        log.info("Добавили кнопки к сообщению");
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Ошибка при отправке сообщения с клавиатурой пользователю");
        }
    }

    private List<List<InlineKeyboardButton>> getMenu() {
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        for (int i = 1; i <= countOfCategories; i++) {
            List<InlineKeyboardButton> rowInline = new ArrayList<>();
            var category = categoryRepository.findById(i);
            var button = new InlineKeyboardButton();

            if (category.isPresent()) {
                button.setText(category.get().getText());
                button.setCallbackData(category.get().getText());
                rowInline.add(button);
                rowsInline.add(rowInline);
            } else {
                log.error("Категория не найдена");
            }
        }
        return rowsInline;
    }

    private void showDocumentsCatalogue(Long chatId, Integer messageId) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        // Переменная для отслеживания количества кнопок в текущем ряду
        List<InlineKeyboardButton> currentRow = new ArrayList<>();

        for (int i = 1; i <= countOfDocuments; i++) {
            var document = documentsRepository.findById(i);
            if (document.isPresent()) {
                String document_name = document.get().getName();
                currentRow.add(createButton(document_name));

                // Если текущий ряд содержит 2 кнопки, добавляем его в список и создаем новый ряд
                if (currentRow.size() == 2) {
                    rowsInline.add(currentRow);
                    currentRow = new ArrayList<>(); // Создаем новый ряд
                }
            }
        }

        // Если остались кнопки в последнем ряду, добавляем их
        if (!currentRow.isEmpty()) {
            rowsInline.add(currentRow);
        }

        markup.setKeyboard(rowsInline);

        EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup();
        editMessageReplyMarkup.setChatId(chatId);
        editMessageReplyMarkup.setMessageId(messageId);
        editMessageReplyMarkup.setReplyMarkup(markup);

        EditMessageText editText = new EditMessageText();
        editText.setChatId(String.valueOf(chatId));
        editText.setMessageId(messageId);
        editText.setText("Выберите документ:");

        try {
            execute(editText); // Редактируем текст сообщения
            execute(editMessageReplyMarkup); // Редактируем кнопки

        } catch (TelegramApiException e) {
            log.error("Ошибка при изменении сообщения в методе получения документов", e);
        }
    }

    private void showOtherOptionsCatalogue(Long chatId, Integer messageId) {

    }

    private void showDigitalServicesCatalogue(Long chatId, Integer messageId) {

    }

    private void showBankServicesCatalogue(Long chatId, Integer messageId) {

    }

    private void ShowFinancialServiceCatalogue(Long chatId, Integer messageId) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();


        // Переменная для отслеживания количества кнопок в текущем ряду
        List<InlineKeyboardButton> currentRow = new ArrayList<>();

        for (int i = 1; i <= countOfFinancialServices; i++) {
            var document = financialServiceRepository.findById(i);
            if (document.isPresent()) {
                String documentName = document.get().getName();
                currentRow.add(createButton(documentName));

                // Если текущий ряд содержит 2 кнопки, добавляем его в список и создаем новый ряд
                if (currentRow.size() == 2) {
                    rowsInline.add(currentRow);
                    currentRow = new ArrayList<>(); // Создаем новый ряд
                }
            }
        }

        // Если остались кнопки в последнем ряду, добавляем их
        if (!currentRow.isEmpty()) {
            rowsInline.add(currentRow);
        }

        markup.setKeyboard(rowsInline);

        EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup();
        editMessageReplyMarkup.setChatId(chatId);
        editMessageReplyMarkup.setMessageId(messageId);
        editMessageReplyMarkup.setReplyMarkup(markup);

        EditMessageText editText = new EditMessageText();
        editText.setChatId(String.valueOf(chatId));
        editText.setMessageId(messageId);
        editText.setText("Выберите финансовую услугу:");

        try {
            execute(editText); // Редактируем текст сообщения
            execute(editMessageReplyMarkup); // Редактируем кнопки

        } catch (TelegramApiException e) {
            log.error("Ошибка при изменении сообщения в методе получения финансовых услуг", e);
        }
    }


    // Вспомогательный метод для создания кнопки
    private InlineKeyboardButton createButton(String name) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(name);
        button.setCallbackData(name);
        return button;
    }
}
