package org.example;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class KalinaBot extends TelegramLongPollingBot {

    //словарь с айди и датой
    private final Map<Long, LocalDate> lastStickerDate = new HashMap<>();

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();
            Long userId = message.getFrom().getId();
            LocalDate today = LocalDate.now();
            // проверка что пользователь не получал сегодня шедевральный стикер
            if (!lastStickerDate.containsKey(userId) || !lastStickerDate.get(userId).equals(today)) {
                // запуск калины бедолаге
                SendSticker sticker = new SendSticker();
                sticker.setChatId(userId);
                sticker.setSticker(new InputFile("CAACAgIAAxkBAAEBQ2xkheMuY5I1fHhqU1xz5cYxFY8HwAACRgADVp29CqHETpoHRi3hNAQ"));


                SendMessage text = new SendMessage();
                text.setChatId(userId);
                text.setText("Такая калина ты сегодня! 🌸");

                try {
                    execute(sticker);
                    execute(text);
                    lastStickerDate.put(userId, today);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else {
                SendMessage alreadySent = new SendMessage();
                alreadySent.setChatId(userId);
                alreadySent.setText("Ты уже получал(а) калину сегодня 🌺");

                try {
                    execute(alreadySent);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public String getBotUsername() {
        return "YourKalinaBot"; // Имя бота
    }

    @Override
    public String getBotToken() {
        return "7789374063:AAHVT5SGQFdUxAQbHO-VaYzYxDXEKeV_upE";
    }

    public static void main(String[] args) {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new KalinaBot());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}

