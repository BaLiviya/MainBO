package baliviya.com.github.MBO.command.impl;

import baliviya.com.github.MBO.command.Command;
import baliviya.com.github.MBO.entity.custom.Group;
import baliviya.com.github.MBO.entity.enums.WaitingType;
import baliviya.com.github.MBO.services.ReportService;
import baliviya.com.github.MBO.utils.ButtonsLeaf;
import baliviya.com.github.MBO.utils.Const;
import baliviya.com.github.MBO.utils.DateKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class id028_Statistic extends Command {

    private List<Group>       groups;
    private ArrayList<String> listNames;
    private ButtonsLeaf       buttonsLeaf;
    private int               deleteMessageId;
    private int               groupId;
    private DateKeyboard      dateKeyboard;
    private Date              dateBegin;
    private Date              dateEnd;
    private int               messagePreviewReport;

    @Override
    public boolean execute() throws TelegramApiException, SQLException {
        if (!isAdmin()) {
            sendMessage(Const.NO_ACCESS);
            return EXIT;
        }
        switch (waitingType) {
            case START:
                deleteMessage(updateMessageId);
                listNames = new ArrayList<>();
                groups = factory.getGroupDao().getGroupsOrder();
                groups.forEach((e) -> listNames.add(e.getNames() + space + (e.isRegistered() ? "| Вкл" : "|Откл")));
                buttonsLeaf = new ButtonsLeaf(listNames, 6, prevButton, nextButton);
                if (listNames.size() == 0) {
                    deleteMessageId = sendMessage("Групп нет.");
                    return EXIT;
                } else {
                    toDeleteKeyboard(sendMessageWithKeyboard("Количество групп " + groups.size() + next + "Выберите группу для отчета", buttonsLeaf.getListButton()));
                }
                waitingType = WaitingType.CHOOSE_OPTION;
                return COMEBACK;
            case CHOOSE_OPTION:
                deleteMessage(updateMessageId);
                deleteMessage(deleteMessageId);
                if (hasCallbackQuery()) {
                    if (buttonsLeaf.isNext(updateMessageText)) {
                        toDeleteKeyboard(sendMessageWithKeyboard("Количество групп " + groups.size() + next + "Выберите группу для отчета", buttonsLeaf.getListButton()));
                    } else {
                        groupId = groups.get(Integer.parseInt(updateMessageText)).getId();
                        dateKeyboard = new DateKeyboard();
                        toDeleteKeyboard(sendMessageWithKeyboard("Выберите начальную дату", dateKeyboard.getCalendarKeyboard()));
                        waitingType = WaitingType.START_DATE;
                    }
                }
                return COMEBACK;
            case START_DATE:
                deleteMessage(updateMessageId);
                if (hasCallbackQuery()) {
                    if (dateKeyboard.isNext(updateMessageText)) {
                        toDeleteKeyboard(sendMessageWithKeyboard("Выберите начальную дату", dateKeyboard.getCalendarKeyboard()));
                    } else {
                        dateBegin = dateKeyboard.getDateDate(updateMessageText);
                        toDeleteKeyboard(sendMessageWithKeyboard("Выберите конечную дату", dateKeyboard.getCalendarKeyboard()));
                        waitingType = WaitingType.END_DATE;
                    }
                }
                return COMEBACK;
            case END_DATE:
                deleteMessage(updateMessageId);
                if (hasCallbackQuery()) {
                   if (dateKeyboard.isNext(updateMessageText)) {
                       toDeleteKeyboard(sendMessageWithKeyboard("Выберите конечную дату", dateKeyboard.getCalendarKeyboard()));
                   } else {
                       dateEnd = dateKeyboard.getDateDate(updateMessageText);
                       messagePreviewReport = sendMessage("Отчет подготавливается...");
                       new Thread(() -> {
                               new ReportService().sendReports(chatId, bot, dateBegin, dateEnd, groupId, messagePreviewReport);
                       }).start();

                   }
                }
                return COMEBACK;
        }
        return EXIT;
    }
}
