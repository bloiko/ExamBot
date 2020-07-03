package com.bohdan;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

public interface Exam {
    public void launchTasks(String tasksFile,String aswersFile);
   // public int startExam(Bot bot, Message message, Update update);
    public String getTask(int i) ;
    public String getAnswer(int i) ;
    public int size();//number of tasks
    public int checkTest(List<String> answers);
}
