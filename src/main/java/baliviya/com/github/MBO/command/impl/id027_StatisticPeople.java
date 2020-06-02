package baliviya.com.github.MBO.command.impl;

import baliviya.com.github.MBO.command.Command;
import baliviya.com.github.MBO.entity.enums.WaitingType;
import baliviya.com.github.MBO.utils.Const;
import baliviya.com.github.MBO.utils.DateKeyboard;
import baliviya.com.github.MBO.utils.components.DataRec;
import org.joda.time.DateTime;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.text.SimpleDateFormat;

public class id027_StatisticPeople extends Command {

    private DateKeyboard dateKeyboard;
    private DateTime     dateBegin;
    private DateTime     dateEnd;

    @Override
    public boolean execute() throws TelegramApiException, SQLException {
        if (!isAdmin()) {
            sendMessage(Const.NO_ACCESS);
            return EXIT;
        }
        switch (waitingType) {
            case START:
                deleteMessage(updateMessageId);
                dateKeyboard = new DateKeyboard();
                toDeleteKeyboard(sendMessageWithKeyboard("Выберите начальную дату ", dateKeyboard.getCalendarKeyboard()));
                waitingType = WaitingType.START_DATE;
                return COMEBACK;
            case START_DATE:
                deleteMessage(updateMessageId);
                if (dateKeyboard.isNext(updateMessageText)) {
                    if (dateBegin == null) {
                        toDeleteKeyboard(sendMessageWithKeyboard("Выберите начальную дату ", dateKeyboard.getCalendarKeyboard()));
                    } else {
                        toDeleteKeyboard(sendMessageWithKeyboard("Выберите конечную дату ", dateKeyboard.getCalendarKeyboard()));
                    }
                    return COMEBACK;
                }
                if (dateBegin == null) {
                    dateBegin = new DateTime(dateKeyboard.getCalendarDate(updateMessageText).getTimeInMillis());
                    toDeleteKeyboard(sendMessageWithKeyboard("Выберите конечную дату ", dateKeyboard.getCalendarKeyboard()));
                    return COMEBACK;
                } else {
                    StringBuilder stringBuilder = new StringBuilder();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.YY");
                    dateEnd = new DateTime(dateKeyboard.getCalendarDate(updateMessageText).getTimeInMillis());
                    String dateBegins = dateFormat.format(dateBegin.toDate());
                    String dateEnds   = dateFormat.format(dateEnd.toDate());
                    stringBuilder.append("Список активных жителей за ").append(dateBegins).append(" - ").append(dateEnds).append(next);
                    int i = 0;
                    for (DataRec rec : taskDao.getCountPeople(dateBegin.toDate(), dateEnd.toDate())) {
                        i++;
                        if (i < 11) stringBuilder.append(i).append(") ").append(rec.getString("PEOPLE_NAME")).append(" - " + rec.getInt("TOTAL")).append(next);
                    }
                    sendMessage(stringBuilder.toString());
                }
        }
        return EXIT;
    }
}
