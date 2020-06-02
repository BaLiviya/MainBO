package baliviya.com.github.MBO.config;

import baliviya.com.github.MBO.command.Command;
import baliviya.com.github.MBO.command.impl.id021_GroupManager;
import baliviya.com.github.MBO.dao.DaoFactory;
import baliviya.com.github.MBO.dao.impl.ButtonDao;
import baliviya.com.github.MBO.dao.impl.MessageDao;
import baliviya.com.github.MBO.entity.enums.Language;
import baliviya.com.github.MBO.entity.standart.Button;
import baliviya.com.github.MBO.entity.standart.Message;
import baliviya.com.github.MBO.exceptions.CommandNotFoundException;
import baliviya.com.github.MBO.services.CommandService;
import baliviya.com.github.MBO.services.LanguageService;
import baliviya.com.github.MBO.utils.Const;
import baliviya.com.github.MBO.utils.DateUtil;
import baliviya.com.github.MBO.utils.SetDeleteMessages;
import baliviya.com.github.MBO.utils.UpdateUtil;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.Date;

@Slf4j
public class Conversation {

    private        Long           chatId;
    private        DaoFactory     factory        = DaoFactory.getInstance();
    private        MessageDao     messageDao;
    private        ButtonDao      buttonDao;
    private static long           currentChatId;
    private        Command        command;
    private        CommandService commandService = new CommandService();

    public void handleUpdate(Update update, DefaultAbsSender bot) throws TelegramApiException, SQLException {
        printUpdate(update);
        chatId = UpdateUtil.getChatId(update);
        currentChatId = chatId;
        messageDao = factory.getMessageDao();
        buttonDao = factory.getButtonDao();
        org.telegram.telegrambots.meta.api.objects.Message updateMessage = update.getMessage();
        String inputtedText = null;
        if (updateMessage != null && updateMessage.hasText()) {
            inputtedText = updateMessage.getText();
        }
        try {
            if (chatId < 0) {
                command = new id021_GroupManager();
                commandExecute(update, bot);
                return;
            }
            if (chatId > 0) {
                checkLanguage(chatId);
            }
        } catch (NullPointerException e) {
            log.error("Error in command check Conversation class or 021_class", e);
        }
        if (update.hasCallbackQuery()) {
            inputtedText = update.getCallbackQuery().getData().split(Const.SPLIT)[0];
            updateMessage = update.getCallbackQuery().getMessage();
            try {
                if (inputtedText != null && inputtedText.substring(0, 3).equals(Const.ID_MARK)) {
                    try {
                        inputtedText = buttonDao.getButtonText(Integer.parseInt(inputtedText.split(Const.SPLIT)[0].replaceAll("/id", "")));
                    } catch (NumberFormatException e) {
                        inputtedText = updateMessage.getText();
                    }
                }
            } catch (Exception e) {}
        }
        inputtedText = regForGroup(update, inputtedText);
        Button button;
        try {
            button = buttonDao.getButton(inputtedText);
            command = commandService.getCommand(button);
            if (command != null) {
                SetDeleteMessages.deleteKeyboard(chatId, bot);
                SetDeleteMessages.deleteMessage(chatId, bot);
            }
        } catch (CommandNotFoundException e) {
            if (command == null) {
                SetDeleteMessages.deleteKeyboard(chatId, bot);
                SetDeleteMessages.deleteMessage(chatId, bot);
                Message message = messageDao.getMessage(Const.COMMAND_NOT_FOUND);
                SendMessage sendMessage = new SendMessage(chatId, message.getName());
                bot.execute(sendMessage);
            }
        }
        commandExecute(update, bot);
    }

    private String regForGroup(Update update, String inputtedText) throws SQLException {
        if (update.hasMessage() && update.getMessage().hasText() && UpdateUtil.getChatId(update) > 0) {
            try {
                String[] split = update.getMessage().getText().split(" ");
                if (split[0].equals("/start") && split[1] != null && !split[1].isEmpty()) {
                    return buttonDao.getButton(11).getName();
                }
            } catch (Exception e) {}
        }
        return inputtedText;
    }

    private void commandExecute(Update update, DefaultAbsSender bot) throws SQLException, TelegramApiException {
        if (command.isInitNormal(update, bot)) {
            clear();
            return;
        }
        boolean commandFinished = command.execute();
        if (commandFinished) {
            clear();
        }
    }

    public static long getCurrentChatId() {
        return currentChatId;
    }

    private void checkLanguage(long chatId) {
        if (LanguageService.getLanguage(chatId) == null) {
            LanguageService.setLanguage(chatId, Language.ru);
        }
    }

    private void printUpdate(Update update) {
        String dataMessage = "";
        if (update.hasMessage()) {
            dataMessage = DateUtil.getDbMmYyyyHhMmSs(new Date((long) update.getMessage().getDate() * 1000));
        }
        log.debug("New update get {} -> send response {}", dataMessage, DateUtil.getDbMmYyyyHhMmSs(new Date()));
        log.debug(UpdateUtil.toString(update));
    }

    private void clear() {
        command.clear();
        command = null;
    }
}
