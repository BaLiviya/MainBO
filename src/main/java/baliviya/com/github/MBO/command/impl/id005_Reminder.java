package baliviya.com.github.MBO.command.impl;

import baliviya.com.github.MBO.command.Command;
import baliviya.com.github.MBO.entity.custom.Group;
import baliviya.com.github.MBO.entity.custom.ReminderTask;
import baliviya.com.github.MBO.entity.enums.WaitingType;
import baliviya.com.github.MBO.utils.ButtonsLeaf;
import baliviya.com.github.MBO.utils.Const;
import baliviya.com.github.MBO.utils.DateKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class id005_Reminder extends Command {

    private List<ReminderTask>  reminderTaskList;
    private int                 deleteMessageId;
    private int                 reminderTaskId;
    private DateKeyboard        dateKeyboard;
    private Date                dateStart;
    private boolean             isUpdate = false;
    private ReminderTask        reminderTask;
    private List<String>        list;
    private ButtonsLeaf         buttonsLeaf;
    private String              tableName;

    @Override
    public boolean  execute()                           throws TelegramApiException, SQLException {
        if (!isAdmin()) {
            sendMessage(Const.NO_ACCESS);
            return EXIT;
        }
        switch (waitingType) {
            case START:
                deleteMessage(updateMessageId);
                deleteMessageId = getDistrict();
                waitingType     = WaitingType.SET_DISTRICT;
                return COMEBACK;
            case SET_DISTRICT:
                deleteMessage(updateMessageId);
                deleteMessage(deleteMessageId);
                if (hasCallbackQuery()) {
                    switch (list.get(Integer.parseInt(updateMessageText))) {
                        case "Медеуский район" :
                            tableName = "";
                            break;
                        case "Турксибский район":
                            tableName = "";
                            break;
                        case "Наурызбайский район" :
                            tableName = "NAUR";
                            break;
                        case "Жетисуский район":
                            tableName = "";
                            break;
                        case "Ауезовский район":
                            tableName = "";
                            break;
                        case "Алатауский район":
                            tableName = "";
                            break;
                        case "Бостандыкский район":
                            tableName = "";
                            break;
                        case "Алмалинский район":
                            tableName = "";
                            break;
                        case "Все":
                            tableName = "PUBLIC";
                            break;
                    }
                    sendListReminder(tableName);
                    waitingType = WaitingType.CHOOSE_OPTION;
                }
                return COMEBACK;
            case CHOOSE_OPTION:
                deleteMessage(deleteMessageId);
                deleteMessage(updateMessageId);
                if (hasMessageText()) {
                    if (isCommand("/new")) {
                        dateKeyboard     = new DateKeyboard();
                        deleteMessageId  = sendStartDate();
                        waitingType      = WaitingType.START_DATE;
                    } else if (isCommand("/del")) {
                        reminderTaskId   = reminderTaskList.get(Integer.parseInt(updateMessageText.replaceAll("[^0-9]",""))).getId();
                        factory.getReminderTaskDao().delete(reminderTaskId, tableName);
                        sendListReminder(tableName);
                        waitingType      = WaitingType.CHOOSE_OPTION;
                    } else if (isCommand("/st")) {
                        reminderTaskId   = reminderTaskList.get(Integer.parseInt(updateMessageText.replaceAll("[^0-9]",""))).getId();
                        dateKeyboard     = new DateKeyboard();
                        sendStartDate();
                        isUpdate         = true;
                        waitingType      = WaitingType.START_DATE;
                    }
                }
                return COMEBACK;
            case START_DATE:
                deleteMessage(updateMessageId);
                deleteMessage(deleteMessageId);
                if (hasCallbackQuery()) {
                    if (dateKeyboard.isNext(updateMessageText)) {
                        sendStartDate();
                        return COMEBACK;
                    }
                    dateStart        = dateKeyboard.getDateDate(updateMessageText);
                    dateStart.setHours(0);
                    dateStart.setMinutes(0);
                    dateStart.setSeconds(0);
                    if (isUpdate) {
                        reminderTask = factory.getReminderTaskDao().getById(reminderTaskId, tableName);
                    } else {
                        reminderTask = new ReminderTask();
                    }
                    reminderTask.setDateBegin(dateStart);
                    deleteMessageId = sendMessage(Const.SEND_MESSAGE_TEXT_MESSAGE);
                    waitingType     = WaitingType.SET_TEXT;
                }
                return COMEBACK;
            case SET_TEXT:
                deleteMessage(updateMessageId);
                deleteMessage(deleteMessageId);
                if (hasMessageText()) {
                    reminderTask.setText(updateMessageText);
                    if (isUpdate) {
                        factory.getReminderTaskDao().update(reminderTask, tableName);
                        sendMessageToGroup();
                    } else {
                        factory.getReminderTaskDao().insert(reminderTask, tableName);
                        sendMessageToGroup();
                    }
                    sendListReminder(tableName);
                    waitingType = WaitingType.CHOOSE_OPTION;
                }
                return COMEBACK;
        }
        return COMEBACK;
    }

    private void    sendMessageToGroup()                throws TelegramApiException {
        if (tableName.equals("PUBLIC")) {
            final List<Group> groups = factory.getGroupDao().getGroups();
            for (Group group : groups) {
                sendMessage(reminderTask.getText(), group.getChatId());
            }
        } else {
            long groupChatId = factory.getGroupDao().getGroupToId(Integer.parseInt(factory.getPropertiesDao().getPropertiesValue(3, tableName))).getChatId();
            sendMessage(reminderTask.getText(), groupChatId);
        }
    }

    private int     getDistrict()                       throws TelegramApiException {
        list        = new ArrayList<>();
        Arrays.asList(getText(Const.DISTRICT_TYPE_MESSAGE).split(Const.SPLIT)).forEach(e -> list.add(e));
        list.add("Все");
        buttonsLeaf = new ButtonsLeaf(list);
        return toDeleteKeyboard(sendMessageWithKeyboard(getText(Const.SELECT_DISTRICT_MESSAGE), buttonsLeaf.getListButton()));
    }

    private void    sendListReminder(String tableName)  throws TelegramApiException {
        String formatMessage        = getText(Const.REMINDER_EDIT_MESSAGE);
        StringBuilder stringBuilder = new StringBuilder();
        reminderTaskList            = factory.getReminderTaskDao().getAll(tableName);
        String format               = getText(Const.REMINDER_EDIT_SINGLE_MESSAGE);
        for (int i = 0; i < reminderTaskList.size(); i++) {
            ReminderTask reminderTask = reminderTaskList.get(i);
            stringBuilder.append(String.format(format, "/del" + i, "/st" + i, reminderTask.getText())).append(next);
        }
        deleteMessageId = sendMessage(String.format(formatMessage, stringBuilder.toString(), "/new"));
    }

    private int     sendStartDate()                     throws TelegramApiException { return toDeleteKeyboard(sendMessageWithKeyboard(getText(Const.SELECT_START_DATE_MESSAGE), dateKeyboard.getCalendarKeyboard())); }

    private boolean isCommand(String command) { return updateMessageText.startsWith(command); }
}
