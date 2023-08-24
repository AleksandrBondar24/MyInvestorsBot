package bondar.MyInvestorsBot.service;

import bondar.MyInvestorsBot.config.BotConfig;
import bondar.MyInvestorsBot.model.Currency;
import bondar.MyInvestorsBot.model.Securities;
import bondar.MyInvestorsBot.model.User;
import bondar.MyInvestorsBot.repository.UserRepository;
import com.vdurmont.emoji.EmojiParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class InvestorBot extends TelegramLongPollingBot {
    private final UserRepository userRepository;

    private static final String HELP_TEXT = "Основные команды данного бота: \n\n" +
            "/start, получить приветственное сообщение\n\n" +
            "/mydata, сохранить мои данные\n\n" +
            "/deletedata, удалить мои данные\n\n" +
            "/help, информация о том как использвать этого бота\n\n" +
            "/settings, установить свои предпочтения\n\n" +
            "/currencyrate, получить курс рубля\n\n" +
            "/priceticker, получить стоимость ценной бумаги";

    private final BotConfig botConfig;

    @Autowired
    public InvestorBot(UserRepository userRepository, BotConfig botConfig) {
        this.userRepository = userRepository;
        this.botConfig = botConfig;
        List<BotCommand> commands = new ArrayList<>();
        commands.add(new BotCommand("/start", "получить приветственное сообщение"));
        commands.add(new BotCommand("/mydata", "сохранить мои данные"));
        commands.add(new BotCommand("/deletedata", "удалить мои данные"));
        commands.add(new BotCommand("/help", "информация о том как использвать этого бота"));
        commands.add(new BotCommand("/settings", "установить свои предпочтения"));
        commands.add(new BotCommand("/currencyrate", "получить курс рубля"));
        commands.add(new BotCommand("/priceticker", "получить стоимость ценной бумаги"));

        try {
            this.execute(new SetMyCommands(commands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Ошибка настройки списка команд бота: " + e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getKey();
    }

    @Override
    public void onUpdateReceived(Update update) {
        Currency currency = new Currency();
        Securities securities = new Securities();
        String botResponse = "";
        String defaultText = "Извините, такой команды нет";
        if (update.hasMessage() && update.getMessage().hasText()) {
            final String messageText = update.getMessage().getText();
            final String name = update.getMessage().getChat().getFirstName();
            long chatId = update.getMessage().getChatId();

            switch (messageText) {
                case "/start":
                    registerUser(update.getMessage());
                    startCommandReceived(chatId, name);
                    break;
                case "/currencyrate":
                    try {
                        botResponse = BotRequestsService.getCurrencyRate(messageText, currency);
                    } catch (NumberFormatException | IOException e) {
                        sendMessage(chatId, "Мы не нашли такой валюты." + "\n" +
                                "Введите валюту, официальный курс которой" + "\n" +
                                "вы хотите узнать относительно RUB." + "\n" +
                                "Например: USD");
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                    sendMessage(chatId, botResponse);
                    break;
                case "/priceticker":
                    try {
                        botResponse = BotRequestsService.getSecuritiesRate(messageText, securities);
                    } catch (NumberFormatException | IOException e) {
                        sendMessage(chatId, "Мы не нашли такого тикера ценной бумаги." + "\n" +
                                "Введите тикер ценной бумаги" + "\n" +
                                "цену которой хотите узнать." + "\n" +
                                "Например: INMD");
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                    sendMessage(chatId, botResponse);
                    break;
                case "/help":
                    sendMessage(chatId, HELP_TEXT);
                    break;
                default:
                    sendMessage(chatId, defaultText);
            }
        }
    }

    private void registerUser(Message message) {
        var idChat = message.getChatId();
        if (!userRepository.existsById(idChat)) {
            userRepository.save(User.builder()
                    .idChat(idChat)
                    .firstName(message.getChat().getFirstName())
                    .lastName(message.getChat().getLastName())
                    .userName(message.getChat().getUserName())
                    .registerTime(LocalDateTime.now())
                    .build());
            log.info("Сохранен пользователь: {}", idChat);
        }
    }

    private void startCommandReceived(long chatId, String name) {
        String response = EmojiParser.parseToUnicode("Привет, " + name + ", рад нашему знакомству!" + " :blush:");
        //String response = "Привет, " + name + ", рад нашему знакомству!";
        sendMessage(chatId, response);
        log.info("Поприветствовал пользователя: " + name);
    }

    private void sendMessage(long chatId, String response) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(response);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Произошла ошибка: " + e.getMessage());
        }
    }
}
