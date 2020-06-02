package baliviya.com.github.MBO.command.impl;

import baliviya.com.github.MBO.command.Command;
import baliviya.com.github.MBO.entity.enums.WaitingType;
import baliviya.com.github.MBO.entity.standart.User;
import baliviya.com.github.MBO.utils.Const;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.SQLException;

public class id031_FindUser extends Command {

    private User user;

    @Override
    public boolean execute() throws TelegramApiException, SQLException {
        if (!isAdmin()) {
            sendMessage(Const.NO_ACCESS);
            return EXIT;
        }
        switch (waitingType) {
            case START:
                deleteMessage(updateMessageId);
                sendMessage("Пришлите сообщение от пользователя");
                waitingType = WaitingType.FIND_USER;
                return COMEBACK;
            case FIND_USER:
                if (update.hasMessage()) {
                    if (updateMessage.getForwardFrom() != null) {
                        long chatForward = updateMessage.getForwardFrom().getId();
                        if (!userDao.isRegistered(chatForward)) {
                            sendMessage("Не зарегистрирован");
                            return COMEBACK;
                        }
                        user = userDao.getUserByChatId(chatForward);
                        sendMessage(String.format(getText(1057), user.getFullName(), user.getUserName(), user.getPhone()));
                        return COMEBACK;
                    }
                }
                return COMEBACK;
        }
        return COMEBACK;
    }
}
