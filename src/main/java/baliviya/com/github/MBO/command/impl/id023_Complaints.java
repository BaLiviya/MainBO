package baliviya.com.github.MBO.command.impl;

import baliviya.com.github.MBO.command.Command;
import baliviya.com.github.MBO.entity.custom.Suggestion;
import baliviya.com.github.MBO.entity.enums.WaitingType;
import baliviya.com.github.MBO.entity.standart.User;
import baliviya.com.github.MBO.utils.Const;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.Date;

public class id023_Complaints extends Command {

    private Suggestion suggestion;
    private int        deleteMessageId;
    private User       user;

    @Override
    public boolean execute()    throws TelegramApiException, SQLException {
        switch (waitingType) {
            case START:
                deleteMessage(updateMessageId);
                user            = userDao.getUserByChatId(chatId);
                if (user == null) {
                    sendMessage("Пройдите регистрацию в районном боте или через групповой чат");
                    return EXIT;
                }
                suggestion      = new Suggestion();
                suggestion      .setFullName(user.getFullName());
                suggestion      .setPostDate(new Date());
                suggestion      .setPhoneNumber(user.getPhone());
                deleteMessageId = getComplaint();
                waitingType     = WaitingType.SET_COMPLAINT;
                return COMEBACK;
            case SET_COMPLAINT:
                deleteMessage(updateMessageId);
                deleteMessage(deleteMessageId);
                if (hasMessageText()) {
                    suggestion      .setText(updateMessageText);
                    suggestionDao   .insertComplaint(suggestion);
                    sendMessage(Const.COMPLAINT_DONE_MESSAGE);
                    return EXIT;
                } else {
                    wrongData();
                    getComplaint();
                    return COMEBACK;
                }
//                deleteMessageId = getFullName();
//                waitingType = WaitingType.SET_FULL_NAME;
//                return COMEBACK;
//            case SET_FULL_NAME:
//                deleteMessage(updateMessageId);
//                deleteMessage(deleteMessageId);
//                if (hasMessageText() && update.getMessage().getText().length() <= 50) {
//                    suggestion = new Suggestion();
//                    suggestion.setFullName(updateMessageText);
//                    suggestion.setPostDate(new Date());
//                    deleteMessageId = getPhone();
//                    waitingType = WaitingType.SET_PHONE_NUMBER;
//                } else {
//                    wrongData();
//                    getFullName();
//                }
//                return COMEBACK;
//            case SET_PHONE_NUMBER:
//                deleteMessage(updateMessageId);
//                deleteMessage(deleteMessageId);
//                if (botUtils.hasContactOwner(update)) {
//                    suggestion.setPhoneNumber(update.getMessage().getContact().getPhoneNumber());
//                    deleteMessageId = getEmail();
//                    waitingType = WaitingType.SET_EMAIL;
//                } else {
//                    wrongData();
//                    getPhone();
//                }
//                return COMEBACK;
//            case SET_EMAIL:
//                deleteMessage(updateMessageId);
//                deleteMessage(deleteMessageId);
//                if (hasMessageText()) {
//                    suggestion.setEmail(updateMessageText);
//                    deleteMessageId = getComplaint();
//                    waitingType = WaitingType.SET_COMPLAINT;
//                } else {
//                    wrongData();
//                    getEmail();
//                }
//                return COMEBACK;
//            case SET_COMPLAINT:
//                deleteMessage(updateMessageId);
//                deleteMessage(deleteMessageId);
//                if (hasMessageText()) {
//                    suggestion.setText(updateMessageText);
//                    suggestionDao.insertComplaint(suggestion);
//                    sendMessage(Const.COMPLAINT_DONE);
//                    return EXIT;
//                } else {
//                    wrongData();
//                    getComplaint();
//                    return COMEBACK;
//                }
        }
        return EXIT;
    }

    private int wrongData()     throws TelegramApiException {
        return botUtils.sendMessage(Const.WRONG_DATA_TEXT, chatId);
    }

    private int getComplaint()  throws TelegramApiException {
        return botUtils.sendMessage(Const.SET_COMPLAINT_MESSAGE, chatId);
    }
}
