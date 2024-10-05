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
import java.util.concurrent.ConcurrentHashMap;
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

    private final Map<Long, Deque<MenuState>> userStates = new ConcurrentHashMap<>();

   @Value("${catalogue.count-of-categories}")
    private int countOfCategories;


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

            if (message.contains("/send") && isOwner(chatId)) {
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
            switch (callBackData) {
                case "Документы":
                    showCatalogue(
                            chatId, messageId,
                            documentsRepository, getNameExtractorForState(MenuState.DOCUMENTS_MENU),
                            getChoiceTextForState(MenuState.DOCUMENTS_MENU),
                            MenuState.DOCUMENTS_MENU, true
                    );
                    break;
                case "Финансовые услуги":
                    showCatalogue(
                            chatId, messageId,
                            financialServiceRepository, getNameExtractorForState(MenuState.FINANCIAL_SERVICES_MENU),
                            getChoiceTextForState(MenuState.FINANCIAL_SERVICES_MENU),
                            MenuState.FINANCIAL_SERVICES_MENU, true
                    );
                    break;
                case "Банковские услуги":
                    showCatalogue(
                            chatId, messageId,
                            bankServiceRepository, getNameExtractorForState(MenuState.BANK_SERVICES_MENU),
                            getChoiceTextForState(MenuState.BANK_SERVICES_MENU),
                            MenuState.BANK_SERVICES_MENU, true
                    );
                    break;
                case "Цифровые услуги":
                    showCatalogue(
                            chatId, messageId,
                            digitalServiceRepository, getNameExtractorForState(MenuState.DIGITAL_SERVICES_MENU),
                            getChoiceTextForState(MenuState.DIGITAL_SERVICES_MENU),
                            MenuState.DIGITAL_SERVICES_MENU, true
                    );
                    break;
                case "Прочее":
                    showCatalogue(
                            chatId, messageId,
                            otherServiceRepository, getNameExtractorForState(MenuState.OTHER_SERVICES_MENU),
                            getChoiceTextForState(MenuState.OTHER_SERVICES_MENU),
                            MenuState.OTHER_SERVICES_MENU, true
                    );
                    break;
                case "Акции":
                    showCatalogue(
                            chatId, messageId,
                            saleRepository, getNameExtractorForState(MenuState.SALES_MENU),
                            getChoiceTextForState(MenuState.SALES_MENU),
                            MenuState.SALES_MENU, true
                    );
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

    private void backForOneStep(Long chatId, Integer messageId) {
        Deque<MenuState> stack = userStates.get(chatId);
        if (stack == null || stack.isEmpty()) {
            log.warn("Стек состояний пуст или не инициализирован для chatId: {}", chatId);
            getProductsCatalogue(chatId); // Возвращаем к основному каталогу
            return;
        }

        // Удаляем текущее состояние
        stack.pop();

        if (stack.isEmpty()) {
            // Если стек пуст, возвращаем к основному меню
            getProductsCatalogue(chatId);
            return;
        }

        // Получаем предыдущее состояние
        MenuState previousState = stack.peek();

        // В зависимости от состояния вызываем соответствующий метод
        switch (previousState) {
            case MAIN_MENU:
                backToProductsCatalogue(chatId, messageId);
                break;
            case DOCUMENTS_MENU:
                showCatalogue(
                        chatId, messageId,
                        documentsRepository, getNameExtractorForState(MenuState.DOCUMENTS_MENU),
                        getChoiceTextForState(MenuState.DOCUMENTS_MENU),
                        MenuState.DOCUMENTS_MENU, false
                );
                break;
            case FINANCIAL_SERVICES_MENU:
                showCatalogue(
                        chatId, messageId,
                        financialServiceRepository, getNameExtractorForState(MenuState.FINANCIAL_SERVICES_MENU),
                        getChoiceTextForState(MenuState.FINANCIAL_SERVICES_MENU),
                        MenuState.FINANCIAL_SERVICES_MENU, false
                );
                break;
            case BANK_SERVICES_MENU:
                showCatalogue(
                        chatId, messageId,
                        bankServiceRepository, getNameExtractorForState(MenuState.BANK_SERVICES_MENU),
                        getChoiceTextForState(MenuState.BANK_SERVICES_MENU),
                        MenuState.BANK_SERVICES_MENU, false
                );
                break;
            case DIGITAL_SERVICES_MENU:
                showCatalogue(
                        chatId, messageId,
                        digitalServiceRepository, getNameExtractorForState(MenuState.DIGITAL_SERVICES_MENU),
                        getChoiceTextForState(MenuState.DIGITAL_SERVICES_MENU),
                        MenuState.DIGITAL_SERVICES_MENU, false
                );
                break;
            case OTHER_SERVICES_MENU:
                showCatalogue(
                        chatId, messageId,
                        otherServiceRepository, getNameExtractorForState(MenuState.OTHER_SERVICES_MENU),
                        getChoiceTextForState(MenuState.OTHER_SERVICES_MENU),
                        MenuState.OTHER_SERVICES_MENU, false
                );
                break;
            case SALES_MENU:
                showCatalogue(
                        chatId, messageId,
                        saleRepository, getNameExtractorForState(MenuState.SALES_MENU),
                        getChoiceTextForState(MenuState.SALES_MENU),
                        MenuState.SALES_MENU, false
                );
                break;
            case ITEM_DETAILS:
                // Возвращаемся к соответствующему каталогу
                // Для упрощения возвращаемся к предыдущему каталогу
                if (stack.size() >= 2) { // Проверяем, есть ли ещё одно предыдущее состояние
                    stack.pop(); // Удаляем ITEM_DETAILS
                    MenuState catalogState = stack.peek();
                    showCatalogue(
                            chatId, messageId,
                            getRepositoryForState(catalogState),
                            getNameExtractorForState(catalogState),
                            getChoiceTextForState(catalogState),
                            catalogState, false
                    );
                } else {
                    backToProductsCatalogue(chatId, messageId);
                }
                break;
            // Добавьте другие состояния по мере необходимости
            default:
                getProductsCatalogue(chatId);
                break;
        }
    }

    // Вспомогательные методы для получения репозитория, экстрактора имени и текста выбора на основе состояния
    private <T> CrudRepository<T, Integer> getRepositoryForState(MenuState state) {
        return switch (state) {
            case DOCUMENTS_MENU -> (CrudRepository<T, Integer>) documentsRepository;
            case FINANCIAL_SERVICES_MENU -> (CrudRepository<T, Integer>) financialServiceRepository;
            case BANK_SERVICES_MENU -> (CrudRepository<T, Integer>) bankServiceRepository;
            case DIGITAL_SERVICES_MENU -> (CrudRepository<T, Integer>) digitalServiceRepository;
            case OTHER_SERVICES_MENU -> (CrudRepository<T, Integer>) otherServiceRepository;
            case SALES_MENU -> (CrudRepository<T, Integer>) saleRepository;
            default -> null;
        };
    }

    private Function<Object, String> getNameExtractorForState(MenuState state) {
        switch (state) {
            case DOCUMENTS_MENU:
                return obj -> ((Document) obj).getName();
            case FINANCIAL_SERVICES_MENU:
                return obj -> ((FinancialService) obj).getName();
            case BANK_SERVICES_MENU:
                return obj -> ((BankService) obj).getName();
            case DIGITAL_SERVICES_MENU:
                return obj -> ((DigitalService) obj).getName();
            case OTHER_SERVICES_MENU:
                return obj -> ((OtherService) obj).getName();
            case SALES_MENU:
                return obj -> ((Sale) obj).getName();
            default:
                return obj -> "Unknown";
        }
    }

    private String getChoiceTextForState(MenuState state) {
        switch (state) {
            case DOCUMENTS_MENU:
                return "Выберите документ";
            case FINANCIAL_SERVICES_MENU:
                return "Выберите финансовую услугу";
            case BANK_SERVICES_MENU:
                return "Выберите банковскую услугу";
            case DIGITAL_SERVICES_MENU:
                return "Выберите цифровую услугу";
            case OTHER_SERVICES_MENU:
                return "Выберите услугу";
            case SALES_MENU:
                return "Выберите акцию";
            default:
                return "Выберите опцию";
        }
    }

    private void backToProductsCatalogue(Long chatId, Integer messageId) {
        // Очищаем стек состояний и устанавливаем MAIN_MENU
        Deque<MenuState> stack = userStates.computeIfAbsent(chatId, k -> new ArrayDeque<>());
        stack.clear();
        stack.push(MenuState.MAIN_MENU);

        EditMessageText message = getEditMessageTextForBackToProductsCatalogue(chatId, messageId);

        try {
            execute(message); // Изменяем текущее сообщение на главное меню
        } catch (TelegramApiException e) {
            log.error("Ошибка при изменении сообщения на начальное меню: {}", e.getMessage());
        }
    }

    private EditMessageText getEditMessageTextForBackToProductsCatalogue(Long chatId, Integer messageId) {
        EditMessageText message = new EditMessageText();
        message.setChatId(chatId.toString());
        message.setMessageId(messageId);

        // Устанавливаем текст для главного меню
        message.setText("Ниже приведен каталог услуг, которые мы предоставляем.");

        // Создаём инлайн-клавиатуру с основными категориями
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = getMenu(); // Используем существующий метод для создания кнопок
        inlineKeyboardMarkup.setKeyboard(rowsInline);

        message.setReplyMarkup(inlineKeyboardMarkup);
        return message;
    }

    private void showPrice(Long chatId, Integer messageId, String callbackData) {
        // Сохраняем состояние деталей элемента
        Deque<MenuState> stack = userStates.computeIfAbsent(chatId, k -> new ArrayDeque<>());
        stack.push(MenuState.ITEM_DETAILS);

        EditMessageText message = new EditMessageText();
        message.setChatId(chatId.toString());
        message.setMessageId(messageId);

        // Универсальный поиск элемента по названию
        Optional<? extends PricedItem> itemInfo = findItemByName(callbackData);

        // Обработка найденного элемента
        itemInfo.ifPresentOrElse(
                item -> {
                    message.setText("Цена на товар \"%s\": %d\n\n".formatted(item.getName(), item.getPrice()) +
                            "Договориться о приобретении: %s\n\n".formatted("@zmcbqpryf") +
                            "За каждого приведенного друга даем 10% от суммы его покупки!");

                    // Создание инлайн-клавиатуры с кнопками
                    InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
                    List<List<InlineKeyboardButton>> rowsInline = getListsForShowPrice();

                    markup.setKeyboard(rowsInline);
                    message.setReplyMarkup(markup);
                },
                () -> {
                    message.setText("Элемент не найден");
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

    private static List<List<InlineKeyboardButton>> getListsForShowPrice() {
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        // Кнопка "Назад"
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
        return rowsInline;
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

            // Инициализируем стек состояний с MAIN_MENU
            Deque<MenuState> stack = new ArrayDeque<>();
            stack.push(MenuState.MAIN_MENU);
            userStates.put(chatId, stack);
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
            log.error("Ошибка при отправке сообщения с клавиатурой пользователю", e);
        }

        // Устанавливаем состояние MAIN_MENU
        Deque<MenuState> stack = userStates.computeIfAbsent(chatId, k -> new ArrayDeque<>());
        stack.clear();
        stack.push(MenuState.MAIN_MENU);
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


    private void showCatalogue(
            Long chatId,
            Integer messageId,
            CrudRepository<?, Integer> repository,
            Function<Object, String> nameExtractor,
            String choiceText,
            MenuState newState,
            boolean pushState // Новый параметр
    ) {

        // Сохраняем новое состояние только если pushState == true
        if (pushState) {
            Deque<MenuState> stack = userStates.computeIfAbsent(chatId, k -> new ArrayDeque<>());
            stack.push(newState);
        }

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

        // Добавляем кнопку "Назад"
        InlineKeyboardButton backButton = new InlineKeyboardButton();
        backButton.setText("Назад");
        backButton.setCallbackData("Назад"); // Убедитесь, что это значение будет обработано
        rowsInline.add(Collections.singletonList(backButton)); // Добавляем кнопку "Назад"

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
            log.error("Ошибка при изменении сообщения в методе showCatalogue", e);
        }
    }


    private Optional<? extends PricedItem> findItemByName(String callbackData) {
        List<Function<String, Optional<? extends PricedItem>>> repositoryFinders = List.of(
                documentsRepository::findByName,
                financialServiceRepository::findByName,
                bankServiceRepository::findByName,
                digitalServiceRepository::findByName,
                otherServiceRepository::findByName,
                saleRepository::findByName
        );

        // Итерируем по всем репозиториям и возвращаем первый найденный элемент
        for (Function<String, Optional<? extends PricedItem>> finder : repositoryFinders) {
            Optional<? extends PricedItem> item = finder.apply(callbackData);
            if (item.isPresent()) {
                return item;
            }
        }
        return Optional.empty();
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

    // Вспомогательный метод для создания кнопки
    private InlineKeyboardButton createButton(String name) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(name);
        button.setCallbackData(name);
        return button;
    }

    private boolean isOwner(Long chatId) {
        return (
                Objects.equals(botConfig.getOwner2ChatId(), chatId) ||
                        Objects.equals(botConfig.getOwnerChatId(), chatId) ||
                        Objects.equals(botConfig.getOwner3ChatId(), chatId)
        );
    }

}
