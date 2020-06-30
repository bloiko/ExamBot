import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.meta.generics.LongPollingBot;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;

public class Bot extends TelegramLongPollingBot {
    FileWriter usersNameFile;

    public static void main(String[] args) {
        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try {
            telegramBotsApi.registerBot((LongPollingBot) new Bot());
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }
    }

    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        if (message != null && message.hasText()) {
            if (message.getText().equals("/info")) {
                sendMsg(message, "Ви знаходитесь в боті,\n" +
                        "у якому ви проходите екзамен.\n" +
                        "Призові місця нагороджуються(100грн, 50грн, 30грн)\n" +
                        "Спочатку зареєструйтесь за допомогою /register\n" +
                        "Надішліть посилання @TryToEarn_bot п'ятьом друзям\n" +
                        "Пізніше натисніть /exam щоб розпочати екзамен");
            } else if (message.getText().equals("/exam")) {
                boolean examExist = false;
                if (examExist == false) {
                    sendMsg(message, "Екзамен розпочнеться 1 липня");
                } else {
                    sendMsg(message, "Екзамен розпочався");
                    sendMsg(message, "Правила");//To Do
                    //int score = startExam();
                }

            } else if (message.getText().equals("/start")) {
                sendMsg(message, "Привіт, я ExamBot\n" +
                        "Пропоную тобі заробляти на своїх знаннях");
            } else if (message.getText().equals("/register")) {
                String userName = message.getFrom().getUserName();
                sendMsg(message, userName);
                try {
                    FileOutputStream fos = new FileOutputStream("Users.txt");
                    byte[] buffer = userName.getBytes();
                    fos.write(buffer, 0, buffer.length);

                    // usersNameFile = new FileWriter("Users.txt");
                    //usersNameFile.write(userName+"\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println(userName);

            } else {

            }
        }

    }

    public String getBotUsername() {
        return "ExamBot";
    }

    public String getBotToken() {
        return "1392710998:AAGZJY87XbV8Sl5D7e8AkyTQrvBMfaL11_8";
    }

    private void sendMsg(Message message, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setReplyToMessageId(message.getMessageId());
        sendMessage.setText(text);
        try {
            //sendMessage(sendMessage);
            final Serializable execute = execute(new SendMessage(message.getChatId(), text));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
