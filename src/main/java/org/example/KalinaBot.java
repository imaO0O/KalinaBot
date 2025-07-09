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
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.InputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class KalinaBot extends TelegramLongPollingBot {

    //словарь с айди и датой
    private final Map<Long, LocalDate> lastStickerDate = new HashMap<>();
    private final List<String> stickerIds = loadStickerIdsFromJson();
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
                String randomId = stickerIds.get((int)(Math.random()*stickerIds.size()));

                sticker.setSticker(new InputFile(randomId));


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
        return "KalinaBot"; // Имя бота
    }

    @Override
    public String getBotToken() {
        return "7789374063:AAHVT5SGQFdUxAQbHO-VaYzYxDXEKeV_upE";
    }

    private List<String> loadStickerIdsFromJson() {
        ObjectMapper mapper = new ObjectMapper();
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("stickers.json")) {
            JsonNode root = mapper.readTree(is);
            JsonNode stickersArray = root.path("stickers");
            List<String> ids = new ArrayList<>();
            for (JsonNode node : stickersArray) {
                ids.add(node.get("file_id").asText());
            }
            return ids;
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
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

