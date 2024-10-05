package com.telegrambot.forfedos.services;

import com.telegrambot.forfedos.config.BotConfig;
import com.telegrambot.forfedos.models.*;
import com.telegrambot.forfedos.repositories.*;
import com.vdurmont.emoji.EmojiParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
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
import java.util.function.Function;

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
    private final SaleRepository saleRepository;


    @Value("${catalogue.count-of-categories}")
    private Integer countOfCategories;

    @Value("${catalogue.count-of-documents}")
    private Integer countOfDocuments;

    @Value("${catalogue.count-of-financial-services}")
    private Integer countOfFinancialServices;

    @Value("${catalogue.count-of-bank-services}")
    private Integer countOfBankServices;

    @Value("${catalogue.count-of-digital-services}")
    private Integer countOfDigitalServices;

    @Value("${catalogue.count-of-other-services}")
    private Integer countOfOtherServices;

    @Value("${catalogue.count-of-sales}")
    private Integer countOfSales;

    private final Map<Long, Stack<String>> customerMessagesHistory = new HashMap<>();


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

            if (message.contains("/send") && (Objects.equals(botConfig.getOwner2ChatId(), chatId) || Objects.equals(botConfig.getOwnerChatId(), chatId) | Objects.equals(botConfig.getOwner3ChatId(), chatId))) {
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
                    showCatalogue(chatId, messageId, documentsRepository, Document::getName, "Выберите документ");
                    break;
                case "Финансовые услуги":
                    showCatalogue(chatId, messageId, financialServiceRepository, FinancialService::getName, "Выберите финансовую услугу");
                    break;
                case "Банковские услуги":
                    showCatalogue(chatId, messageId, bankServiceRepository, BankService::getName, "Выберите банковскую услугу");
                    break;
                case "Цифровые услуги":
                    showCatalogue(chatId, messageId, digitalServiceRepository, DigitalService::getName, "Выберите цифровую услугу");
                    break;
                case "Прочее":
                    showCatalogue(chatId, messageId, otherServiceRepository, OtherService::getName, "Выберите услугу");
                    break;
                case "Акции":
                    showCatalogue(chatId, messageId, saleRepository, Sale::getName, "Выберите акцию");
                    break;
                case "Назад":
                    backForOneStep(chatId, messageId);
                    break;
                case "Главное меню":
                    backToProductsCatalogue(chatId, messageId);
                    break;
                default:
                    showPrice(chatId, messageId, callBackData);
                    break;
            }
        }
    }

    private void addMessageToHistory(Long chatId, String callbackData) {
        customerMessagesHistory.putIfAbsent(chatId, new Stack<>());
        customerMessagesHistory.get(chatId).push(callbackData);
    }

    private void backForOneStep(Long chatId, Integer messageId) {
        Stack<String> history = customerMessagesHistory.get(chatId);

        if (history.isEmpty()) {
            log.error("Попытка возврата назад на главном меню");
            return;
        }

        String previousMessage = history.pop();

        EditMessageText message = new EditMessageText();
        message.setChatId(chatId);
        message.setMessageId(messageId);
        message.setText(previousMessage);
    }

    private void backToProductsCatalogue(Long chatId, Integer messageId) {
        EditMessageText message = new EditMessageText();
        message.setChatId(chatId.toString());
        message.setMessageId(messageId);

        // Set the text for the initial menu
        message.setText("Ниже приведен каталог услуг, которые мы предоставляем.");

        // Create the inline keyboard markup
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = getMenu(); // Reuse getMenu() to create buttons
        inlineKeyboardMarkup.setKeyboard(rowsInline);

        message.setReplyMarkup(inlineKeyboardMarkup);

        try {
            execute(message); // Edit the current message with the new menu
        } catch (TelegramApiException e) {
            log.error("Ошибка при изменении сообщения на начальное меню: {}", e.getMessage());
        }
    }

    private boolean isSale(String callbackData) {
        List<String> sales = new ArrayList<>();
        for (int i = 1; i <= countOfSales; i++) {
            String documentText = saleRepository.findById(i).get().getName();
            if (saleRepository.findById(i).isPresent()) {
                sales.add(documentText);
            } else {
                log.error("Документ не найден [isSale]");
            }
        }
        return sales.contains(callbackData);
    }

    private boolean isDocument(String callbackData) {
        List<String> documents = new ArrayList<>();
        for (int i = 1; i <= countOfDocuments; i++) {
            String documentText = documentsRepository.findById(i).get().getName();
            if (documentsRepository.findById(i).isPresent()) {
                documents.add(documentText);
            } else {
                log.error("Документ не найден [isDocument]");
            }
        }
        return documents.contains(callbackData);
    }

    private boolean isFinancialService(String callbackData) {
        List<String> financialServices = new ArrayList<>();
        for (int i = 1; i <= countOfFinancialServices; i++) {
            String documentText = financialServiceRepository.findById(i).get().getName();
            if (financialServiceRepository.findById(i).isPresent()) {
                financialServices.add(documentText);
            } else {
                log.error("Документ не найден [isFinancialService]");
            }
        }
        return financialServices.contains(callbackData);
    }

    private boolean isBankService(String callbackData) {
        List<String> bankServices = new ArrayList<>();
        for (int i = 1; i <= countOfBankServices; i++) {
            String documentText = bankServiceRepository.findById(i).get().getName();
            if (bankServiceRepository.findById(i).isPresent()) {
                bankServices.add(documentText);
            } else {
                log.error("Документ не найден [isBankService]");
            }
        }
        return bankServices.contains(callbackData);
    }

    private boolean isDigitalService(String callbackData) {
        List<String> digitalServices = new ArrayList<>();
        for (int i = 1; i <= countOfDigitalServices; i++) {
            String documentText = digitalServiceRepository.findById(i).get().getName();
            if (digitalServiceRepository.findById(i).isPresent()) {
                digitalServices.add(documentText);
            } else {
                log.error("Документ не найден [isDigitalService]");
            }
        }
        return digitalServices.contains(callbackData);
    }

    private boolean isOtherService(String callbackData) {
        List<String> otherServices = new ArrayList<>();
        for (int i = 1; i <= countOfBankServices; i++) {
            String documentText = otherServiceRepository.findById(i).get().getName();
            if (otherServiceRepository.findById(i).isPresent()) {
                otherServices.add(documentText);
            } else {
                log.error("Документ не найден [isOtherService]");
            }
        }
        return otherServices.contains(callbackData);
    }


    private void showPrice(Long chatId, Integer messageId, String callbackData) {
        EditMessageText message = new EditMessageText();
        message.setChatId(chatId.toString());
        message.setMessageId(messageId);

        Optional<? extends PricedItem> itemInfo = Optional.empty();

        // Поиск данных в репозиториях
        if (isDocument(callbackData)) {
            itemInfo = documentsRepository.findByName(callbackData);
        } else if (isFinancialService(callbackData)) {
            itemInfo = financialServiceRepository.findByName(callbackData);
        } else if (isBankService(callbackData)) {
            itemInfo = bankServiceRepository.findByName(callbackData);
        } else if (isDigitalService(callbackData)) {
            itemInfo = digitalServiceRepository.findByName(callbackData);
        } else if (isOtherService(callbackData)) {
            itemInfo = otherServiceRepository.findByName(callbackData);
        } else if (isSale(callbackData)) {
            itemInfo = saleRepository.findByName(callbackData);
        }

        // Обработка данных
        itemInfo.ifPresentOrElse(
                item -> {
                    message.setText("Цена на товар \"%s\": %d\n\n".formatted(item.getName(), item.getPrice()) +
                            "Договориться о приобретении: %s\n\n".formatted("@zmcbqpryf") +
                            "За каждого приведенного друга даем 10% от суммы его покупки!");

                    // Create the inline keyboard with a back button
                    InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
                    List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

                    // Back button to return to the main menu
                    InlineKeyboardButton backToMainMenuButton = new InlineKeyboardButton();
                    backToMainMenuButton.setText("К главному меню");
                    backToMainMenuButton.setCallbackData("Главное меню");

                    InlineKeyboardButton backForOneStepButton = new InlineKeyboardButton();
                    backForOneStepButton.setText("Назад");
                    backForOneStepButton.setCallbackData("Назад");

                    List<InlineKeyboardButton> rowInline = new ArrayList<>();
                    rowInline.add(backForOneStepButton);
                    rowInline.add(backToMainMenuButton);
                    rowsInline.add(rowInline);

                    markup.setKeyboard(rowsInline);
                    message.setReplyMarkup(markup);
                },
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

    // Вспомогательный метод для создания кнопки
    private InlineKeyboardButton createButton(String name) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(name);
        button.setCallbackData(name);
        return button;
    }

    private <T> void showCatalogue(
            Long chatId,
            Integer messageId,
            CrudRepository<T, Integer> repository,
            Function<T, String> nameExtractor,
            String choiceText) {

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        // Переменная для отслеживания количества кнопок в текущем ряду
        List<InlineKeyboardButton> currentRow = new ArrayList<>();

        // Получаем количество объектов из репозитория
        long countOfItems = repository.count();

        for (int i = 1; i <= countOfItems; i++) {
            // Ищем объект по идентификатору
            var item = repository.findById(i);

            // Проверяем, существует ли объект
            if (item.isPresent()) {
                // Используем переданный nameExtractor для извлечения имени объекта
                String itemName = nameExtractor.apply(item.get());
                currentRow.add(createButton(itemName));

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

        // Устанавливаем клавиатуру с кнопками
        markup.setKeyboard(rowsInline);

        // Обновляем текст сообщения
        EditMessageText editText = new EditMessageText();
        editText.setChatId(String.valueOf(chatId));
        editText.setMessageId(messageId);
        editText.setText(choiceText);

        // Обновляем кнопки сообщения
        EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup();
        editMessageReplyMarkup.setChatId(chatId);
        editMessageReplyMarkup.setMessageId(messageId);
        editMessageReplyMarkup.setReplyMarkup(markup);

        try {
            execute(editText); // Редактируем текст сообщения
            execute(editMessageReplyMarkup); // Редактируем кнопки
        } catch (TelegramApiException e) {
            log.error("Ошибка при изменении сообщения в методе", e);
        }
    }
}
