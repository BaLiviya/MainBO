package baliviya.com.github.MBO.command.impl;

import baliviya.com.github.MBO.command.Command;
import baliviya.com.github.MBO.entity.custom.Receiver;
import baliviya.com.github.MBO.entity.enums.WaitingType;
import baliviya.com.github.MBO.utils.ButtonsLeaf;
import baliviya.com.github.MBO.utils.Const;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class id025_CreateReceiver extends Command {

    private Receiver     receiver;
    private List<String> list;
    private ButtonsLeaf  buttonsLeaf;
    private int          deleteMessageId;

    @Override
    public boolean  execute()        throws TelegramApiException, SQLException {
        switch (waitingType) {
            case START:
                deleteMessage(updateMessageId);
                receiver        = new Receiver();
                receiver.setDate(new Date());
                receiver.setChatId(chatId);
                deleteMessageId = getReceiver();
                waitingType     = WaitingType.RECEIVER_TYPE;
                return COMEBACK;
            case RECEIVER_TYPE:
                delete();
                if (hasCallbackQuery()) {
                    if (list.get(Integer.parseInt(updateMessageText)).equals("Другое")) {
                        deleteMessageId = getOther();
                        waitingType     = WaitingType.OTHER_RECEIVER_TYPE;
                    } else {
                        receiver.setCharityType(list.get(Integer.parseInt(updateMessageText)));
                        deleteMessageId = getText();
                        waitingType     = WaitingType.SET_TEXT;
                    }
                }
                return COMEBACK;
            case OTHER_RECEIVER_TYPE:
                delete();
                if (hasMessageText()) {
                    receiver.setCharityType(updateMessageText);
                    deleteMessageId = getText();
                    waitingType     = WaitingType.SET_TEXT;
                }
                return COMEBACK;
            case SET_TEXT:
                delete();
                if (hasMessageText()) {
                    receiver.setText(updateMessageText);
                    deleteMessageId = getDoneMessage();
                    waitingType     = WaitingType.DONE_DONATE;
                }
                return COMEBACK;
            case DONE_DONATE:
                delete();
                if (isButton(Const.SELECT_BUTTON)) {
                    factory.getReceiverDao().insert(receiver);
                    sendMessageWithKeyboard(getText(Const.DONE_HELP_MESSAGE), Const.MAIN_MENU_KEYBOARD);
                    return EXIT;
                }
                return COMEBACK;
        }
        return EXIT;
    }

    private int     getReceiver()    throws TelegramApiException {
        list = new ArrayList<>();
        Arrays.asList("Денежный;Медицинский;Продуктовый;Вещевой;Другое".split(Const.SPLIT)).forEach(e -> list.add(e));
        buttonsLeaf = new ButtonsLeaf(list);
        return toDeleteKeyboard(sendMessageWithKeyboard(getText(Const.HELP_TYPE_SELECT_MESSAGE), buttonsLeaf.getListButton()));
    }

    private int     getOther()       throws TelegramApiException {
        return botUtils.sendMessage(getText(Const.WRITE_HELP_TYPE_MESSAGE), chatId);
    }

    private int     getText()        throws TelegramApiException {
        return botUtils.sendMessage(getText(Const.REASON_YOU_NEED_HELP_MESSAGE), chatId);
    }

    private int     getDoneMessage() throws TelegramApiException {
        return toDeleteKeyboard(sendMessageWithKeyboard(getReceiverText(), Const.DONATE_KEYBOARD));
    }

    private void    delete() {
        deleteMessage(updateMessageId);
        deleteMessage(deleteMessageId);
    }

    private String  getReceiverText() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(next);
        stringBuilder.append(getItalicsText("Тип благотворительной помощи : ")).append(getBoldText(receiver.getCharityType())).append(next);
        stringBuilder.append(getItalicsText("По причине ")).append(getBoldText(receiver.getText())).append(next);
        stringBuilder.append(getItalicsText("Если данные верны нажмите на кнопку ") + getBoldText("Отправить"));
        return stringBuilder.toString();
    }
}
