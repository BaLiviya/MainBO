package baliviya.com.github.MBO.command.impl;

import baliviya.com.github.MBO.command.Command;
import baliviya.com.github.MBO.entity.enums.WaitingType;
import baliviya.com.github.MBO.entity.standart.User;
import baliviya.com.github.MBO.utils.ButtonsLeaf;
import baliviya.com.github.MBO.utils.Const;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class id022_RegistrationUser extends Command {

    private String       userNameGroup;
    private User         user;
    private int          deleteMessageId;
    private int          secondDeleteMessageId;
    private List<String> list;
    private ButtonsLeaf  buttonsLeaf;

    @Override
    public boolean  execute()           throws TelegramApiException {
        switch (waitingType) {
            case START:
                deleteMessage(updateMessageId);
                deleteKeyboard();
                userNameGroup = updateMessageText.replaceAll("/start", "");
                userNameGroup = userNameGroup.replaceAll(" ", "");
                if (userDao.isRegistered(chatId)) {
                    sendMessage(Const.U_R_REGISTERED_MESSAGE);
                    return EXIT;
                }
                deleteMessageId = getName();
                waitingType     = WaitingType.SET_FULL_NAME;
                return COMEBACK;
            case SET_FULL_NAME:
                deleteMessage(updateMessageId);
                deleteMessage(deleteMessageId);
                if (hasMessageText()) {
                    if (isToLong()) {
                        sendMessage(Const.SET_FULL_NAME_PLEASE_MESSAGE);
                        return COMEBACK;
                    }
                    user = new User();
                    user.setChatId(chatId);
                    user.setFullName(updateMessageText);
                    user.setUserName(update.getMessage().getFrom().getUserName());
                    deleteMessageId       = getPhone();
                    waitingType           = WaitingType.SET_PHONE_NUMBER;
                } else {
                    deleteMessageId       = wrongData();
                    secondDeleteMessageId = getName();
                }
                return COMEBACK;
            case SET_PHONE_NUMBER:
                deleteMessage(updateMessageId);
                deleteMessage(deleteMessageId);
                deleteMessage(secondDeleteMessageId);
                if (hasContactOwner()) {
                    user.setPhone(updateMessagePhone);
                    deleteMessageId       = getIin();
                    waitingType           = WaitingType.SET_IIN;
                } else {
                    deleteMessageId       = wrongData();
                    secondDeleteMessageId = getPhone();
                }
                return COMEBACK;
            case SET_IIN:
                deleteMessage(deleteMessageId);
                deleteMessage(updateMessageId);
                deleteMessage(secondDeleteMessageId);
                try {
                    Long.parseLong(update.getMessage().getText());
                } catch (NumberFormatException e) {
                    deleteMessageId         = wrongIinNotNumber();
                    secondDeleteMessageId   = getIin();
                    return COMEBACK;
                }
                if (update.hasMessage() && update.getMessage().hasText() && update.getMessage().getText().length() == 12) {
                    user.setIin(update.getMessage().getText());
                    deleteMessageId       = getStatus();
                    waitingType           = WaitingType.SET_STATUS;
                } else {
                    deleteMessageId       = wrongData();
                    secondDeleteMessageId = getIin();
                }
                return COMEBACK;
            case SET_STATUS:
                deleteMessage(deleteMessageId);
                deleteMessage(updateMessageId);
                deleteMessage(secondDeleteMessageId);
                if (update.hasCallbackQuery()) {
                    if (list.get(Integer.parseInt(update.getCallbackQuery().getData())).equals(getText(Const.OTHERS_MESSAGE))) {
                        deleteMessageId = getOther();
                        waitingType     = WaitingType.OTHER_STATUS;
                    } else {
                        user.setStatus(list.get(Integer.parseInt(update.getCallbackQuery().getData())));
                        registrationUser();
                        return EXIT;
                    }
                } else {
                    deleteMessageId       = wrongData();
                    secondDeleteMessageId = getStatus();
                }
                return COMEBACK;
            case OTHER_STATUS:
                deleteMessage(deleteMessageId);
                deleteMessage(updateMessageId);
                deleteMessage(secondDeleteMessageId);
                if (update.hasMessage() && update.getMessage().hasText()) {
                    user.setStatus(update.getMessage().getText());
                    registrationUser();
                    return EXIT;
                } else {
                    deleteMessageId       = wrongData();
                    secondDeleteMessageId = getOther();
                }
                return COMEBACK;
        }
        return EXIT;
    }

    private int     getName()           throws TelegramApiException {
        return botUtils.sendMessage(Const.SET_FULL_NAME_MESSAGE, chatId);
    }

    private int     getPhone()          throws TelegramApiException {
        return botUtils.sendMessage(Const.SEND_CONTACT_MESSAGE, chatId);
    }

    private int     wrongData()         throws TelegramApiException {
        return botUtils.sendMessage(Const.WRONG_DATA_TEXT, chatId);
    }

    private int     getIin()            throws TelegramApiException {
        return botUtils.sendMessage(Const.SET_IIN_MESSAGE, chatId);
    }

    private int     wrongIinNotNumber() throws TelegramApiException {
        return botUtils.sendMessage(Const.IIN_WRONG_MESSAGE, chatId);
    }

    private int     getStatus()         throws TelegramApiException {
        list = new ArrayList<>();
        Arrays.asList(getText(Const.STATUS_TYPE_MESSAGE).split(Const.SPLIT)).forEach((e) -> list.add(e));
        list.add(getText(Const.OTHERS_MESSAGE));
        buttonsLeaf = new ButtonsLeaf(list);
        return botUtils.sendMessageWithKeyboard(getText(Const.STATUS_MESSAGE), buttonsLeaf.getListButton(), chatId);
    }

    private void    registrationUser()  throws TelegramApiException {
        userDao.insert(user);
        String returnToGroup = "<a href = \"https://telegram.me/" + userNameGroup + "\">" + "Вернутся в группу" + "</a>";
        sendMessage("Регистрация прошла успешно" + next + returnToGroup);
    }

    private int     getOther()          throws TelegramApiException {
        return botUtils.sendMessage(Const.SET_YOUR_OPTION_MESSAGE, chatId);
    }

    private boolean isToLong()          throws TelegramApiException {
        if (updateMessageText.length() > 50) {
            deleteMessage(updateMessageId);
            sendMessage(String.format("Слишком много символов - %d \n Длинна текста не должна превышать 50 символов.", updateMessageText.length()));
            return EXIT;
        }
        return COMEBACK;
    }
}
