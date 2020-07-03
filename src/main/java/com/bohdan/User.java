package com.bohdan;

import java.util.LinkedList;
import java.util.List;

public class User {
    private String chatId;
    private boolean examStart;
    private List<String> answers;
    private int task;

    public int getTaskNum() {
        return task;
    }

    public void appendTaskByOne() {
        this.task ++;
    }

    public User(){
        answers = new LinkedList<String>();
    }
    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getChatId() {
        return chatId;
    }

    public void addAnswer(String a){
        answers.add(a);
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
