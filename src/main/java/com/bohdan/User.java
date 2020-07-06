package com.bohdan;

import java.util.LinkedList;
import java.util.List;

public class User {
    private String chatId;
    private String login;
    private boolean examStart;
    private List<String> answers;
    private int task;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public int getTaskNum() {
        return task;
    }

    public void appendTaskByOne() {
        this.task ++;
    }
    public void restart(){
        answers = new LinkedList<>();
    }
    public User(){
        answers = new LinkedList<String>();
    }
    public User setChatId(String chatId) {
        this.chatId = chatId;
        return this;
    }

    public String getChatId() {
        return chatId;
    }

    public void addAnswer(String a){
        answers.add(a);
    }

    public void setTask(int task) {
        this.task = task;
    }

    public List<String> getAnswers() {
        return answers;
    }

    public boolean isExamStart() {
        return examStart;
    }

    public void setExamStart(boolean examStart) {
        this.examStart = examStart;
    }
    public static User getUserFromList(LinkedList<User> users, String chatId){
        for(User u : users){
            if(u.getChatId().equals(chatId)){
                return u;
            }
        }
        return null;
    }
}
