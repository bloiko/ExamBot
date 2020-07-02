package com.bohdan;

import com.bohdan.Exam;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.Scanner;

public class FirstExam implements Exam {
    public static LinkedList<String> taskList;
    public static LinkedList<String> answerList;

    public FirstExam(String tasksFile, String aswersFile) {
      taskList = new LinkedList<String>();
        answerList = new LinkedList<String>();
        launchTasks(tasksFile, aswersFile);
    }

    public void launchTasks(String tasksFile, String aswersFile) {
        readTasks(tasksFile);
        readAnswers(aswersFile);
    }
    public void readTasks(String tasksFile) {
        String task = "";

        try {
            FileReader fileReader = new FileReader(tasksFile);
            Scanner scan = new Scanner(fileReader);
            while (scan.hasNextLine()) {
                String line = scan.nextLine();
                if (line.charAt(3) != '-') {
                    task += line + "\n";
                } else {
                    taskList.add(task);
                    task = "";
                }
            }
        } catch (FileNotFoundException e) {
        }
    }
    public void readAnswers(String aswersFile){
        String aswer = "";

        try {
            FileReader fileReader = new FileReader(aswersFile);
            Scanner scan = new Scanner(fileReader);
            while (scan.hasNextLine()) {
                String line = scan.nextLine();
                String[] arr = line.split(" +");
                answerList.add(arr[1]);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    public int startExam(Bot bot, Message message, Update update) {
        int rightAnswersCounter = 0;
        for (int i = 0; i < taskList.size() && i< answerList.size(); i++) {
            bot.sendTask(bot, message, taskList,i);
            String answer = update.getMessage().getText();
            while(answer.length()!=1){
                bot.answerUpdate(update);
                answer = update.getMessage().getText();
            }
           //answer to do
            if (checkAnswer(answer, i)) {
                rightAnswersCounter++;
            }
        }
        return rightAnswersCounter;
    }



    private boolean checkAnswer(String answer, int numOfTask) {
        answer = new String();//read input
        if (answer.equals(answerList.get(numOfTask))) {
            return true;
        } else return false;
    }


}
