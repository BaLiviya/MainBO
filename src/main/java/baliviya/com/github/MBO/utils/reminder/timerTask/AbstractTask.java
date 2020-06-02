package baliviya.com.github.MBO.utils.reminder.timerTask;


import baliviya.com.github.MBO.config.Bot;
import baliviya.com.github.MBO.dao.DaoFactory;
import baliviya.com.github.MBO.dao.impl.ReminderTaskDao;
import baliviya.com.github.MBO.dao.impl.UserDao;
import baliviya.com.github.MBO.utils.reminder.Reminder;

import java.util.TimerTask;

public abstract class AbstractTask extends TimerTask {

    protected Bot               bot;
    protected Reminder          reminder;
    protected DaoFactory        daoFactory;
    protected ReminderTaskDao   reminderTaskDao;
    protected UserDao           userDao;

    public AbstractTask(Bot bot, Reminder reminder) {
        this.bot      = bot;
        this.reminder = reminder;
    }
}
