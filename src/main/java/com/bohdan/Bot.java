package com.bohdan;

import Services.RedisService;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.meta.generics.LongPollingBot;

import java.io.*;
import java.util.*;

public class Bot extends TelegramLongPollingBot {
    FileWriter usersNameFile;
    LinkedList<User> users;
    Exam exam;
    RedisService<Integer> redis;

    public Bot() {
        users = new LinkedList<User>();
        exam = new FirstExam("src/main/resources/data/Tasks", "src/main/resources/data/Answers");
        redis = new RedisService<Integer>();
    }

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
            String chatId = message.getChatId().toString();
            if (message.getText().equals("/info")) {
                showInfo(message);
            } else if (message.getText().equals("/exam")) {
                showExam(message, chatId);
            } else if (message.getText().equals("/start")) {
                sendMsg(message,  "Привіт, я - ExamBot ✌\uD83C\uDFFB\n" +
                        "Пропоную тобі заробляти на своїх знаннях \uD83D\uDCB0\n" +
                        "Список команд та вся необхідна інформація - /info");
            } else if (message.getText().equals("/help")) {
                sendMsg(message, getCommands("src/main/resources/data/commands"));
            } else if (message.getText().equals("/register")) {
                registerUser(message,chatId);
            } else if (message.getText().equals("/stat")) {
                showStat(message);
            } else if (message.getText().equals("/mystat")) {
                showMyStat(message);
            } else {
                sendMsg(message, "???");
            }
        }
        if (update.hasCallbackQuery()) {
            String call_data = update.getCallbackQuery().getData();
            long message_id = update.getCallbackQuery().getMessage().getMessageId();
            long chat_id = update.getCallbackQuery().getMessage().getChatId();
            User user = User.getUserFromList(users, update.getCallbackQuery().getMessage().getChatId().toString());
            if (call_data.length() == 1) {
                outputCallBack(update, user, call_data);
            }

        }

    }

    private void showInfo(Message message) {
        sendMsg(message, "ExamBot - бот, який дозволяє не лише перевірити свої знання,\n" +
                              "а й отримати за це виграш!\n" +
                        "Виконуй завдання, займай призові місця та отримуй 100, 50 та 30 грн відповідно.\n " +
                        "Для того, щоб взяти участь необхідно :\n" +
                         "1. Зареєструватись за допомогою команди /register;\n" +
                        "2. Надіслати посилання @TryToEarn_bot п'ятьом друзям.\n" +
                        "Успіхів!" );
    }

    private void showExam(Message message,String chat_id) {
        boolean examExist = true;
        if (examExist == false) {
            sendMsg(message, "Екзамен розпочнеться 1 липня");
        } //else if (!User.getUserFromList(users, message.getChatId().toString()).isExamStart()) {
        // sendMsg(message, "Екзамен розпочався");
        //sendMsg(message, "Правила");//To do
        User user = User.getUserFromList(users, chat_id);
        user.setExamStart(true);
        user.setTask(0);
        user.restart();
        sendTask(message, user.getTaskNum());
        // } else {
        //     sendMsg(message, "Ви проходили тест");
        // }

    }

    private void registerUser(Message message,String chat_id) {
        String userName = message.getFrom().getLastName() + " " + message.getFrom().getFirstName();
        if (redis.getString(userName) != null) {
            redis.put(userName, 0);
        }
        User user = new User();
        user.setChatId(chat_id).setLogin(userName);
        users.add(user);
        System.out.println(userName);
        sendMsg(message, "Ви зареєстровані, можете розпочинати екзамен /exam");
    }

    private void showStat(Message message) {
        String myMessage = "Статистика:\n";
        List<String> list = getFirstThree(exam.size());
        for (int i = 0; i < list.size() && i<10; i++)
            myMessage += (i + 1) + ". " + list.get(i) + " - " + redis.get(list.get(i)).get() + "\n";
        sendMsg(message, myMessage);

    }

    private void showMyStat(Message message) {
        String myMessage = "Твоя статистика:\n";
        Optional<Integer> num = redis.get(message.getFrom().getLastName() + " " + message.getFrom().getFirstName());
        myMessage += message.getFrom().getLastName() + " " + message.getFrom().getFirstName() + " - " + Integer.valueOf(num.get()) + "\n";
        sendMsg(message, myMessage);

    }

    private void outputCallBack(Update update, User user, String call_data) {
        if (call_data.equals("A") || call_data.charAt(0) == 65) {
            user.addAnswer("А");
            System.out.println("А");

        } else if (call_data.equals("Б")) {
            user.addAnswer("Б");
            System.out.println("Б");

        } else if (call_data.equals("В")) {
            user.addAnswer("В");
            System.out.println("В");

        } else if (call_data.equals("Г")) {
            user.addAnswer("Г");
            System.out.println("Г");
        } else if (call_data.equals("Д")) {
            user.addAnswer("Д");
            System.out.println("Д");
        }
        user.appendTaskByOne();
        if (user.getTaskNum() < exam.size()) {
            //sendMsg(update.getCallbackQuery().getMessage(), exam.getTask(user.getTaskNum()) + user.getTaskNum());
            sendTask(update.getCallbackQuery().getMessage(), user.getTaskNum());
        } else {
            sendMsg(update.getCallbackQuery().getMessage(), "Правильних відповідей - " + exam.checkTest(user.getAnswers()) + "/" + exam.size());
            String userName = update.getCallbackQuery().getFrom().getLastName() + " " + update.getCallbackQuery().getFrom().getFirstName();
            redis.put(userName, exam.checkTest(user.getAnswers()));
        }

    }

    public String getBotUsername() {
        return "ExamBot";
    }

    public String getBotToken() {
        return "1392710998:AAGZJY87XbV8Sl5D7e8AkyTQrvBMfaL11_8";
    }

    public String getCommands(String file) {
        String commands = "";
        try {
            FileReader fileReader = new FileReader(file);
            Scanner scan = new Scanner(fileReader);
            while (scan.hasNextLine()) {
                String line = scan.nextLine();
                commands += line + "\n";
            }
        } catch (FileNotFoundException e) {
        }
        return commands;
    }

    public List<String> getFirstThree(int maxNum) {
        List<String> list = new LinkedList<>();
        int i = 1;
        for(int counter =0;counter<=exam.size() && i<10;counter++) {
            for (String key : redis.keys()) {
                Optional<Integer> user = redis.get(key);
                if (user != null && !user.equals(Optional.of(0)) && !key.equals("fromzerotoheroo")) {
                    if (user.get() == maxNum-counter) {
                        list.add(key);
                        i++;
                    }
                }
            }
        }
        return list;
    }

    public void sendMsg(Message message, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setReplyToMessageId(message.getMessageId());
        sendMessage.setText(text);
        try {
            final Serializable execute = execute(new SendMessage(message.getChatId(), text));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendCallBack(Update update, String answer) {
        String call_data = update.getCallbackQuery().getData();
        long message_id = update.getCallbackQuery().getMessage().getMessageId();
        long chat_id = update.getCallbackQuery().getMessage().getChatId();
        EditMessageText new_message = new EditMessageText()
                .setChatId(chat_id)
                .setMessageId(Integer.parseInt(String.valueOf(message_id)))
                .setText(answer);
        try {
            execute(new_message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendTask(Message message, int numOfTask) {
        String text = exam.getTask(numOfTask);
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setReplyToMessageId(message.getMessageId());
       // sendMessage.setText(text).setReplyMarkup(createInlineButtons());
        long chatId = message.getChatId();
        try {
            final Serializable execute = execute(sendInlineKeyBoardMessage(chatId, text));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }



    public static SendMessage sendInlineKeyBoardMessage(long chatId, String text) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();
        inlineKeyboardButton1.setText("A");
        inlineKeyboardButton1.setCallbackData("A");
        inlineKeyboardButton2.setText("В");
        inlineKeyboardButton2.setCallbackData("В");
        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<InlineKeyboardButton>();
        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<InlineKeyboardButton>();
        keyboardButtonsRow1.add(inlineKeyboardButton1);
        keyboardButtonsRow1.add(new InlineKeyboardButton().setText("Б").setCallbackData("Б"));
        keyboardButtonsRow2.add(inlineKeyboardButton2);
        keyboardButtonsRow2.add(new InlineKeyboardButton().setText("Г").setCallbackData("Г"));
        List<List<InlineKeyboardButton>> rowList = new ArrayList<List<InlineKeyboardButton>>();
        rowList.add(keyboardButtonsRow1);
        rowList.add(keyboardButtonsRow2);
        inlineKeyboardMarkup.setKeyboard(rowList);
        return new SendMessage().setChatId(chatId).setText(text).setReplyMarkup(inlineKeyboardMarkup);
    }
}
