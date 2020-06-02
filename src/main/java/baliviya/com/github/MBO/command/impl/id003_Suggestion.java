package baliviya.com.github.MBO.command.impl;

import baliviya.com.github.MBO.command.Command;
import baliviya.com.github.MBO.entity.custom.Suggestion;
import baliviya.com.github.MBO.entity.enums.WaitingType;
import baliviya.com.github.MBO.entity.standart.User;
import baliviya.com.github.MBO.utils.Const;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Date;

public class id003_Suggestion extends Command {

    private Suggestion  suggestion;
    private int         deleteMessageId;
    private User        user;

    @Override
    public boolean  execute()       throws TelegramApiException {
        switch (waitingType) {
            case START:
                deleteMessage(updateMessageId);
                user = userDao.getUserByChatId(chatId);
                if (user == null) {
                    sendMessage("Пройдите регистрацию в районном боте или через групповой чат");
                    return EXIT;
                }
                suggestion      = new Suggestion();
                suggestion.setFullName(user.getFullName());
                suggestion.setPostDate(new Date());
                suggestion.setPhoneNumber(user.getPhone());
                deleteMessageId = getSuggestion();
                waitingType     = WaitingType.SET_SUGGESTION;
                return COMEBACK;
            case SET_SUGGESTION:
                deleteMessage(updateMessageId);
                deleteMessage(deleteMessageId);
                if (hasMessageText()) {
                    suggestion.setText(updateMessageText);
                    suggestionDao.insert(suggestion);
                    sendMessage(Const.SUGGESTION_DONE);
                    return EXIT;
                } else {
                    wrongData();
                    getSuggestion();
                    return COMEBACK;
                }
        }
        return EXIT;
    }

    private int     wrongData()     throws TelegramApiException {
        return botUtils.sendMessage(Const.WRONG_DATA_TEXT, chatId);
    }

    private int     getSuggestion() throws TelegramApiException {
        return botUtils.sendMessage(Const.SET_SUGGESTION_MESSAGE, chatId);
    }
}
