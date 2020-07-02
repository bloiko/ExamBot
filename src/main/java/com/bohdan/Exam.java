package com.bohdan;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface Exam {
    public void launchTasks(String tasksFile,String aswersFile);
    public int startExam(Bot bot, Message message, Update update);
}
