package baliviya.com.github.MBO.command.impl;

import baliviya.com.github.MBO.command.Command;
import baliviya.com.github.MBO.entity.custom.Category;
import baliviya.com.github.MBO.entity.enums.WaitingType;
import baliviya.com.github.MBO.utils.ButtonsLeaf;
import baliviya.com.github.MBO.utils.Const;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class id026_StatisticGroup extends Command {

    private List<Category> categoryList;
    private ButtonsLeaf    buttonsLeaf;
    private String         text;
    private int            categoryId;
    private int            done;
    private int            doing;

    @Override
    public boolean execute() throws TelegramApiException, SQLException {
        if (!isAdmin()) {
            sendMessage(Const.NO_ACCESS);
            return EXIT;
        }
        switch (waitingType) {
            case START:
                deleteMessage(updateMessageId);
                categoryList = categoryDao.getList();
                ArrayList<String> buttonNames = new ArrayList<>();
                int allDone = factory.getTaskDao().getTaskCount(1);
                int allDoing = factory.getTaskDao().getTaskCount(3);
                int total = allDone + allDoing;
                categoryList.forEach((e) -> buttonNames.add(e.getName() + " | " + taskDao.getAllTaskCountCategory(e.getId())));
                buttonsLeaf = new ButtonsLeaf(buttonNames);
                text = getText(allDone, allDoing, total);
                toDeleteKeyboard(sendMessageWithKeyboard(text, buttonsLeaf.getListButton()));
                waitingType = WaitingType.CATEGORY;
                return COMEBACK;
            case CATEGORY:
                deleteMessage(updateMessageId);
                if (hasCallbackQuery()) {
                    if (buttonsLeaf.isNext(updateMessageText)) {
                        toDeleteKeyboard(sendMessageWithKeyboard(text, buttonsLeaf.getListButton()));
                    } else {
                        categoryId = categoryList.get(Integer.parseInt(updateMessageText)).getId();
                        done = taskDao.getTaskCountCategory(categoryId, 1);
                        doing = taskDao.getTaskCountCategory(categoryId, 3);
                        sendMessage(getText(done, doing, done + doing));
                        return EXIT;
                    }
                }
                return COMEBACK;
        }
        return EXIT;
    }

    private String getText(int done, int doing, int total) {
        return "Всего обращений : " + total + next + "В процессе : " + doing + next + "Выполнено : " + done;
    }
}
