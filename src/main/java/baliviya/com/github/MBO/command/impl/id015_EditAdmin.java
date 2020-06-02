package baliviya.com.github.MBO.command.impl;

import baliviya.com.github.MBO.command.Command;
import baliviya.com.github.MBO.entity.standart.User;
import baliviya.com.github.MBO.utils.Const;
import baliviya.com.github.MBO.utils.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Date;
import java.util.List;

@Slf4j
public class id015_EditAdmin extends Command {

    private StringBuilder text;
    private List<Long>    allAdmins;
    private int           message;
    private static String delete;
    private static String deleteIcon;
    private static String showIcon;

    @Override
    public boolean  execute()           throws TelegramApiException {
        if (!isAdmin()) {
            sendMessage(Const.NO_ACCESS);
            return EXIT;
        }
        if (deleteIcon == null) {
            deleteIcon  = getText(1051);
            showIcon    = getText(1052);
            delete      = getText(1053);
        }
        if (message != 0) deleteMessage(message);
        if (hasContact()) {
            registerNewAdmin();
            return COMEBACK;
        }
        if(updateMessageText.contains(delete)) {
            try {
                if (allAdmins.size() > 1) {
                    int numberAdminList = Integer.parseInt(updateMessageText.replaceAll("[^0-9]",""));
                    adminDao.delete(allAdmins.get(numberAdminList));
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        sendEditorAdmin();
        return COMEBACK;
    }

    private boolean registerNewAdmin()  throws TelegramApiException {
        long newAdminChatId = update.getMessage().getContact().getUserID();
        if (!userDao.isRegistered(newAdminChatId)) {
            sendMessage("Пользователь не зарегистрирован в данном боте");
            return EXIT;
        } else {
            if (adminDao.isAdmin(newAdminChatId)) {
                sendMessage("Пользователь уже администратор");
                return EXIT;
            } else {
                User user = userDao.getUserByChatId(newAdminChatId);
                adminDao.addAssistant(newAdminChatId, String.format("%s %s %s", user.getUserName(), user.getPhone(), DateUtil.getDbMmYyyyHhMmSs(new Date())));
                User userAdmin = userDao.getUserByChatId(chatId);
                log.info("{} added new admin - {} ", getInfoByUser(userAdmin), getInfoByUser(user));
                sendEditorAdmin();
            }
        }
        return COMEBACK;
    }

    private void    sendEditorAdmin()   throws TelegramApiException {
        deleteMessage(updateMessageId);
        try {
            getText(true);
            message = sendMessage(String.format(getText(1054), text.toString()));
        } catch (TelegramApiException e) {
            getText(false);
            message = sendMessage(String.format(getText(1054), text.toString()));
        }
        toDeleteMessage(message);
    }

    private String  getInfoByUser(User user) {
        return String.format("%s %s %s", user.getFullName(), user.getPhone(), user.getChatId());
    }

    private void    getText(boolean withLink) {
        text        = new StringBuilder();
        allAdmins   = adminDao.getAll();
        int count   = 0;
        for (Long admin : allAdmins) {
            try {
                User user = userDao.getUserByChatId(admin);
                if (allAdmins.size() == 1) {
                    if (withLink) {
                        text.append(getLinkForUser(user.getChatId(), user.getUserName())).append(space).append(next);
                    } else {
                        text.append(getInfoByUser(user)).append(space).append(next);
                    }
                    text.append("Должен быть минимум 1 администратор.").append(next);
                } else {
                    if (withLink) {
                        text.append(delete).append(count).append(deleteIcon).append(" - ").append(showIcon).append(getLinkForUser(user.getChatId(), user.getUserName())).append(space).append(next);
                    } else {
                        text.append(delete).append(count).append(deleteIcon).append(" - ").append(getInfoByUser(user)).append(space).append(next);
                    }
                }
                count++;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
