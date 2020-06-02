package baliviya.com.github.MBO.utils.reminder.timerTask;

import baliviya.com.github.MBO.config.Bot;
import baliviya.com.github.MBO.entity.custom.ReminderTask;
import baliviya.com.github.MBO.entity.standart.User;
import baliviya.com.github.MBO.utils.DateUtil;
import baliviya.com.github.MBO.utils.reminder.Reminder;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Date;
import java.util.List;

public class Task extends AbstractTask {

    private List<User>          users;
    private List<ReminderTask>  tasks;
    private Date                start;

    public Task(Bot bot, Reminder reminder) { super(bot, reminder); }

    @Override
    public void run() {
        users           = userDao.getAll();
        start           = new Date();
        Date dateEnd    = new Date();
        dateEnd.setDate(dateEnd.getDay() + 1);
        tasks           = reminderTaskDao.getByTime(start, dateEnd);
        checkMessage();
        reminder.setMorningTask(17);
    }

    private void checkMessage() {
        if (tasks != null && tasks.size() != 0) {
            users.forEach(this::sendMessage);
        }
    }

    private void sendMessage(User user) {
        tasks.forEach((e) -> {
            if (DateUtil.getDayDate(e.getDateBegin()).equals(DateUtil.getDayDate(start))) {
                try {
                    bot.execute(new SendMessage().setChatId(user.getChatId()).setText(e.getText()));
                } catch (TelegramApiException ex) {}
            }
        });
    }
}
