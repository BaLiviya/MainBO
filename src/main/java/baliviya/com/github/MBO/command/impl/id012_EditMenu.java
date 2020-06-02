package baliviya.com.github.MBO.command.impl;

import baliviya.com.github.MBO.command.Command;
import baliviya.com.github.MBO.entity.enums.FileType;
import baliviya.com.github.MBO.entity.enums.Language;
import baliviya.com.github.MBO.entity.enums.WaitingType;
import baliviya.com.github.MBO.entity.standart.Button;
import baliviya.com.github.MBO.entity.standart.Message;
import baliviya.com.github.MBO.services.LanguageService;
import baliviya.com.github.MBO.utils.ButtonUtil;
import baliviya.com.github.MBO.utils.Const;
import baliviya.com.github.MBO.utils.ParserMessageEntity;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Slf4j
public class id012_EditMenu extends Command {

    private static final String linkEdit = "/linkId";

    private Language currentLanguage;
    private int      buttonId;
    private long     keyboardMarkUpId;
    private Button   currentButton;
    private int      photoId;
    private int      textId;
    private Message  message;
    private int      keyId;
    private boolean  isUrl = false;
    private int      buttonLinkId;

    private final static String NAME = messageDao.getMessageText(Const.NAME_TEXT_FOR_LINK);
    private final static String LINK = messageDao.getMessageText(Const.LINK_TEXT_FOR_EDIT);

    @Override
    public boolean      execute()               throws TelegramApiException {
        if (!isAdmin()) {
            sendMessage(Const.NO_ACCESS);
            return EXIT;
        }
        switch (waitingType) {
            case START:
                deleteMessage(updateMessageId);
                currentLanguage = LanguageService.getLanguage(chatId);
                sendListMenu();
                return COMEBACK;
            case CHOOSE_OPTION:
                deleteMessage(updateMessageId);
                if (hasCallbackQuery()) {
                    buttonId                = Integer.parseInt(updateMessageText);
                    if (buttonDao.getButton(buttonId, currentLanguage).getMessageId() != 0) {
                        keyboardMarkUpId    = messageDao.getMessage(buttonDao.getButton(buttonId, currentLanguage).getMessageId()).getKeyboardMarkUpId();
                    }
                    currentButton           = buttonDao.getButton(buttonId, currentLanguage);
                    sendEditor();
                } else {
                    sendListMenu();
                }
                return COMEBACK;
            case NEXT_KEYBOARD:
                if (hasCallbackQuery()) {
                    buttonId        = Integer.parseInt(updateMessageText);
                    currentButton   = buttonDao.getButton(buttonId, currentLanguage);
                    sendEditor();
                    return COMEBACK;
                } else {
                    sendListMenu();
                }
                return COMEBACK;
            case COMMAND_EDITOR:
                isCommand();
                return COMEBACK;
            case UPDATE_BUTTON:
                if (isCommand()) return COMEBACK;
                if (hasMessageText()) {
                    String buttonName = ButtonUtil.getButtonName(updateMessageText, 100);
                    if (buttonName.replaceAll("[0-9]", "").isEmpty()) {
                        sendMessage(Const.WRONG_NAME_FROM_BUTTON_MESSAGE);
                        return COMEBACK;
                    }
                    if (buttonDao.isExist(buttonName, currentLanguage)) {
                        sendMessage(Const.NAME_IS_ALREADY_IN_USE_MESSAGE);
                        return COMEBACK;
                    }
                    currentButton.setName(buttonName);
                    buttonDao.update(currentButton);
                    sendEditor();
                }
                return COMEBACK;
            case UPDATE_TEXT:
                if (isCommand()) return COMEBACK;
                if (hasMessageText()) {
                    message.setName(new ParserMessageEntity().getTextWithEntity(update.getMessage()));
                    messageDao.update(message);
                    sendEditor();
                }
                return COMEBACK;
            case UPDATE_BUTTON_LINK:
                if (isCommand()) return COMEBACK;
                if (hasMessageText()) {
                    if (updateMessageText.startsWith(NAME)) {
                       String buttonName = ButtonUtil.getButtonName(updateMessageText.replace(NAME,""));
                       if (buttonDao.isExist(buttonName, currentLanguage)) {
                           sendMessage(Const.NAME_IS_ALREADY_IN_USE_MESSAGE);
                           return COMEBACK;
                       }
                       Button button = buttonDao.getButton(buttonLinkId, currentLanguage);
                       button.setName(buttonName);
                       buttonDao.update(button);
                       sendEditor();
                       return COMEBACK;
                    } else if (updateMessageText.startsWith(LINK)) {
                        Button button = buttonDao.getButton(buttonLinkId, currentLanguage);
                        button.setUrl(updateMessageText.replace(LINK,""));
                        buttonDao.update(button);
                        sendEditor();
                        return COMEBACK;
                    }
                }
                sendMessage(Const.MESSAGE_FROM_LINK_EDIT_BUTTON);
                return COMEBACK;
            case UPDATE_FILE:
                if (hasDocument() || hasAudio() || hasVideo()) {
                    if (!isHasMessageForEdit()) return COMEBACK;
                    updateFile();
                    sendMessage(Const.SUCCESS_SEND_FILE_MESSAGE);
                    sendEditor();
                    return COMEBACK;
                }
        }
        return EXIT;
    }

    private void        sendListMenu()          throws TelegramApiException {
        toDeleteKeyboard(sendMessageWithKeyboard(getText(Const.LIST_MENU_EDIT_MESSAGE), keyboardMarkUpDao.selectForEdition(Const.MAIN_MENU_KEYBOARD, currentLanguage)));
        waitingType = WaitingType.CHOOSE_OPTION;
    }

    private void        sendEditor()            throws TelegramApiException {
        clearOld();
        loadElements();
        String desc;
        if (message != null) {
            keyId = (int) message.getKeyboardMarkUpId();
            if (message.getPhoto() != null) {
                photoId = bot.execute(new SendPhoto().setPhoto(message.getPhoto()).setChatId(chatId)).getMessageId();
            }
            StringBuilder urlList       = new StringBuilder();
            if (keyId != 0 && keyboardMarkUpDao.isInline(keyId)) {
                urlList.append(getText(1030)).append(next);
                List<Button> listForEdit = keyboardMarkUpDao.getListForEdit(keyId);
                for (Button button : listForEdit) {
                    urlList.append(linkEdit).append(button.getId()).append(" ").append(button.getName()).append(" - ").append(button.getUrl()).append(next);
                }
            }
            desc = String.format(getText(1031), currentButton.getName(), message.getName(), urlList, currentLanguage.name());
            if (desc.length() > getMaxSizeMessage()) {
                String substring    = message.getName().substring(0, desc.length() - getMaxSizeMessage() - 3) + "...";
                desc                = String.format(getText(1028), currentButton.getName(), substring, currentLanguage.name());
            }
        } else {
            desc    = String.format(getText(1028), currentButton.getName(), getText(1029), currentLanguage.name());
        }
        textId      = sendMessageWithKeyboard(desc,Const.KEYBOARD_EDIT_BUTTON_ID);
        toDeleteKeyboard(textId);
        waitingType = WaitingType.COMMAND_EDITOR;
    }

    private boolean     isCommand()             throws TelegramApiException {
        deleteMessage(updateMessageId);
        if (hasPhoto()) {
            if (!isHasMessageForEdit()) return COMEBACK;
            updatePhoto();
        } else if (hasDocument() || hasAudio() || hasVideo()) {
            if (!isHasMessageForEdit()) return COMEBACK;
            updateFile();
        } else if (isButton(Const.CHANGE_BUTTON_NAME)) {
            sendMessage(Const.ENTER_NEW_NAME_BUTTON_MESSAGE);
            waitingType = WaitingType.UPDATE_BUTTON;
            return EXIT;
        } else if (isButton(Const.CHANGE_BUTTON_TEXT)) {
            if (!isHasMessageForEdit()) return COMEBACK;
            sendMessage(Const.SEND_NEW_MESSAGE_FOR_BUTTON);
            waitingType = WaitingType.UPDATE_TEXT;
            return EXIT;
        } else if (isButton(Const.ADD_FILE_FROM_BUTTON)) {
            sendMessage(Const.SEND_NEW_FILE_MESSAGE);
            waitingType = WaitingType.UPDATE_FILE;
            return EXIT;
        } else if (isButton(Const.DELETE_FILE_FROM_BUTTON)) {
            if (!isHasMessageForEdit()) return COMEBACK;
            deleteFile();
        } else if (isButton(Const.CHANGE_LANGUAGE_BUTTON)) {
            if (currentLanguage == Language.ru) {
                currentLanguage = Language.kz;
            } else {
                currentLanguage = Language.ru;
            }
            currentButton = buttonDao.getButton(buttonId, currentLanguage);
            sendEditor();
            return EXIT;
        } else if (isButton(Const.NEXT_BUTTON)) {
            deleteMessage(updateMessageId);
            deleteMessage(textId);
            if (keyboardMarkUpId != 0) {
                isUrl = getButtonIds((int) keyboardMarkUpId);
            }
            if (keyboardMarkUpId == 2) {
                currentButton = buttonDao.getButton(buttonId, currentLanguage);
                sendEditor();
                return COMEBACK;
            } else if (keyboardMarkUpId > 0) {
                if (!isUrl) {
                    toDeleteKeyboard(sendMessageWithKeyboard(Const.CHOOSE_WHAT_TO_EDIT_MESSAGE, keyboardMarkUpDao.selectForEdition(keyboardMarkUpId, currentLanguage)));
                    waitingType     = WaitingType.NEXT_KEYBOARD;
                } else {
                    currentButton   = buttonDao.getButton(buttonId, currentLanguage);
                    sendEditor();
                    return COMEBACK;
                }
            }
        } else if (updateMessageText.startsWith(linkEdit)) {
            String buttonId     = updateMessageText.replace(linkEdit,"");
            if (keyboardMarkUpDao.getButtonString(keyId).contains(buttonId)) {
                sendMessage(Const.MESSAGE_FROM_LINK_EDIT_BUTTON);
                buttonLinkId    = Integer.parseInt(buttonId);
                waitingType     = WaitingType.UPDATE_BUTTON_LINK;
                return EXIT;
            } else {
                return COMEBACK;
            }
        } else {
            return COMEBACK;
        }
        return EXIT;
    }

    private boolean     isHasMessageForEdit()   throws TelegramApiException {
        if (message == null) {
            sendMessage(Const.THIS_BUTTON_DONT_HAVE_MESSAGE);
            return COMEBACK;
        }
        return EXIT;
    }

    private void        clearOld() {
        deleteMessage(textId);
        deleteMessage(photoId);
    }

    private void        loadElements() {
        if (currentButton.getMessageId() == 0) {
            message = null;
        } else {
            message = messageDao.getMessage(currentButton.getMessageId(), currentLanguage);
        }
    }

    private static int  getMaxSizeMessage() { return Const.MAX_SIZE_MESSAGE; }

    private void        updateFile() {
        if (hasDocument()) {
            message.setFile(update.getMessage().getDocument().getFileId(), FileType.document);
        } else if (hasAudio()) {
            message.setFile(update.getMessage().getAudio().getFileId(), FileType.audio);
        } else if (hasVideo()) {
            message.setFile(update.getMessage().getVideo().getFileId(), FileType.video);
        }
        update();
    }

    private void        updatePhoto() {
        message.setPhoto(updateMessagePhoto);
        update();
    }

    private void        update() {
        messageDao.update(message);
        log.info("Update message {} for lang {} - chatId = ", message.getId(), currentLanguage.name(), chatId);
    }

    private void        deleteFile() {
        message.setFileType(null);
        message.setFile(null);
        update();
    }

    private boolean     getButtonIds(int keyboardMarkUpId) {
        String buttonString = keyboardMarkUpDao.getButtonString(keyboardMarkUpId);
        if (buttonString == null) return COMEBACK;
        String rows[] = buttonString.split(";");
        for (String buttonIdString : rows) {
            String[] buttonIds = buttonIdString.split(",");
            for (String buttonId : buttonIds) {
                Button buttonFromDb = buttonDao.getButton(Integer.parseInt(buttonId), currentLanguage);
                String url          = buttonFromDb.getUrl();
                if (url != null) {
                    return EXIT;
                } else {
                    return COMEBACK;
                }
            }
        }
        return COMEBACK;
    }
}
