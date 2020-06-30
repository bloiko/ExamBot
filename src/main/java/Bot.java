import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.meta.generics.LongPollingBot;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

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
                boolean examExist = true;
                if (examExist == false) {
                    sendMsg(message, "Екзамен розпочнеться 1 липня");
                } else {
                    sendMsg(message, "Екзамен розпочався");
                    sendMsg(message, "Правила");//To do

                    Exam exam = new FirstExam("data/Tasks", "data/Answers");
                    int rightAnswers = exam.startExam(this, message);
                    sendMsg(message, "Правильних відповідей - " + rightAnswers);//To do

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

            }else if(update.hasCallbackQuery()){
                try {
                    final Serializable execute =execute(new SendMessage().setText(
                            update.getCallbackQuery().getData())
                            .setChatId(update.getCallbackQuery().getMessage().getChatId()));
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
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
    public void sendTask(SendMessage message) {
        try {
            //sendMessage(sendMessage);
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    public void sendMsg(Message message, String text) {
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
    public void sendTask(Bot bot, Message message, List<String> taskList, int numOfTask) {
        String text = taskList.get(numOfTask);
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setReplyToMessageId(message.getMessageId());
        sendMessage.setText(text);
        long chatId = message.getChatId();
        //sendMessage.setReplyMarkup(createInlineButtons());
       // bot.sendTask(sendMessage);
        try {
            //final Serializable execute = execute(sendMessage);
            final Serializable execute = execute(sendInlineKeyBoardMessage(chatId,text));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    private InlineKeyboardMarkup createInlineButtons() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton inlineKeyboardButtonA = new InlineKeyboardButton();
        inlineKeyboardButtonA.setText("A");
        InlineKeyboardButton inlineKeyboardButtonB = new InlineKeyboardButton();
        inlineKeyboardButtonB.setText("Б");
        InlineKeyboardButton inlineKeyboardButtonC = new InlineKeyboardButton();
        inlineKeyboardButtonC.setText("В");
        InlineKeyboardButton inlineKeyboardButtonD = new InlineKeyboardButton();
        inlineKeyboardButtonD.setText("Г");

        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<InlineKeyboardButton>();
        keyboardButtonsRow1.add(inlineKeyboardButtonA);
        keyboardButtonsRow1.add(inlineKeyboardButtonB);

        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<InlineKeyboardButton>();
        keyboardButtonsRow2.add(inlineKeyboardButtonC);
        keyboardButtonsRow2.add(inlineKeyboardButtonD);


        List<List<InlineKeyboardButton>> rowList = new ArrayList<List<InlineKeyboardButton>>();
        rowList.add(keyboardButtonsRow1);
        rowList.add(keyboardButtonsRow2);
        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }
    public static SendMessage sendInlineKeyBoardMessage(long chatId,String text) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();
        inlineKeyboardButton1.setText("A");
        inlineKeyboardButton1.setCallbackData("A");
        inlineKeyboardButton2.setText("Г");
        inlineKeyboardButton2.setCallbackData("Г");
        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<InlineKeyboardButton>();
        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<InlineKeyboardButton>();
        keyboardButtonsRow1.add(inlineKeyboardButton1);
        keyboardButtonsRow1.add(new InlineKeyboardButton().setText("Б").setCallbackData("CallFi4a"));
        keyboardButtonsRow2.add(inlineKeyboardButton2);
        keyboardButtonsRow2.add(new InlineKeyboardButton().setText("Д").setCallbackData("CallFi4a"));
        List<List<InlineKeyboardButton>> rowList = new ArrayList<List<InlineKeyboardButton>>();
        rowList.add(keyboardButtonsRow1);
        rowList.add(keyboardButtonsRow2);
        inlineKeyboardMarkup.setKeyboard(rowList);
        return new SendMessage().setChatId(chatId).setText(text).setReplyMarkup(inlineKeyboardMarkup);
    }
}
