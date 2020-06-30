import org.telegram.telegrambots.meta.api.objects.Message;

public interface Exam {
    public void launchTasks(String tasksFile,String aswersFile);
    public int startExam(Bot bot, Message message);
}
