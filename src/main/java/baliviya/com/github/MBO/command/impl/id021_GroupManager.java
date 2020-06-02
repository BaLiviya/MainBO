package baliviya.com.github.MBO.command.impl;

import baliviya.com.github.MBO.command.Command;
import baliviya.com.github.MBO.entity.custom.Group;
import baliviya.com.github.MBO.services.GroupCacheService;
import baliviya.com.github.MBO.utils.UpdateUtil;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.HashSet;

public class id021_GroupManager extends Command {

    private Chat             chat;
    private Group            group;
    private HashSet<Integer> formRegisterMessages = new HashSet<>();

    @Override
    public boolean  execute() throws TelegramApiException, SQLException {
        groupInit();
        if (chat == null || !chat.isSuperGroupChat()) {
            return COMEBACK;
        }
        group = GroupCacheService.checkGroup(update, chatId);
        if (group == null || !group.isRegistered()) {
            return COMEBACK;
        }
        if (!isUserRegistered()) {
            deleteMessage(updateMessageId);
            sendRegistrationMessage();
            return COMEBACK;
        }
        return COMEBACK;
    }

    private void    groupInit() {
        chat = UpdateUtil.getChat(update);
        updateMessage = UpdateUtil.getMessage(update);
        if (updateMessage != null) {
            updateMessageId = updateMessage.getMessageId();
            if (update.hasCallbackQuery()) {
                updateMessageText = update.getCallbackQuery().getData();
            } else if (update.hasMessage()) {
                if (updateMessage.hasText()) {
                    updateMessageText = updateMessage.getText();
                } else if (updateMessage.getCaption() != null) {
                    updateMessageText = update.getMessage().getCaption();
                }
            } else {
                updateMessageText = null;
            }
        }
    }

    private boolean isUserRegistered() {
        return userDao.isRegistered(UpdateUtil.getUser(update).getId());
    }

    private void    sendRegistrationMessage() throws TelegramApiException {
        for (Integer formRegisterMessage : formRegisterMessages) {
            deleteMessage(formRegisterMessage);
        }
        StringBuilder groups = new StringBuilder();
        User user = UpdateUtil.getUser(update);
        String form = (user.getFirstName() != null ? user.getFirstName() + space : "") + (user.getLastName() != null ? user.getLastName() : "");
        if (form.isEmpty()) {
            form = user.getUserName();
        }
        groups.append(getLinkForUser(user.getId(), form));
        groups.append(" чтобы писать в группе, необходимо пройти регистрацию").append(next);
        groups.append(getLinkT(bot.getMe().getUserName() + "?start=" + group.getUserName(), "Регистрация")).append(next);
        formRegisterMessages.add(sendMessage(groups.toString()));
    }

    private String  getLinkT(String link, String text) {
        return new StringBuilder().append("<a href = \"https://t.me/").append(link).append("\">").append(text).append("</a>" + next).toString();
    }
}
