package baliviya.com.github.MBO.command;

import baliviya.com.github.MBO.dao.DaoFactory;
import baliviya.com.github.MBO.dao.impl.*;
import baliviya.com.github.MBO.entity.enums.WaitingType;
import baliviya.com.github.MBO.utils.BotUtil;
import baliviya.com.github.MBO.utils.Const;
import baliviya.com.github.MBO.utils.SetDeleteMessages;
import baliviya.com.github.MBO.utils.UpdateUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.sql.SQLException;
import java.util.Objects;

@Slf4j
@NoArgsConstructor
public abstract class Command {

    @Getter @Setter
    protected long id;
    @Getter @Setter
    protected long messageId;

    protected static       BotUtil              botUtils;
    protected              Update               update;
    protected              DefaultAbsSender     bot;
    protected              Long                 chatId;
    protected              Message              updateMessage;
    protected              String               updateMessageText;
    protected              int                  updateMessageId;
    protected              String               editableTextOfMessage;
    protected              String               updateMessagePhoto;
    protected              String               updateMessagePhone;
    protected              String               markChange;
    protected              int                  lastSendMessageID;
    protected final static boolean              EXIT                        = true;
    protected final static boolean              COMEBACK                    = false;
    protected              WaitingType          waitingType                 = WaitingType.START;
    protected static final String               next                        = "\n";
    protected static final String               space                       = " ";
    protected              String               nextButton                  = ">>";
    protected              String               prevButton                  = "<<";

    protected static DaoFactory        factory           = DaoFactory.getInstance();
    protected static MessageDao        messageDao        = factory.getMessageDao();
    protected static ButtonDao         buttonDao         = factory.getButtonDao();
    protected static KeyboardMarkUpDao keyboardMarkUpDao = factory.getKeyboardMarkUpDao();
    protected static UserDao           userDao           = factory.getUserDao();
    protected static AdminDao          adminDao          = factory.getAdminDao();
    protected static SuggestionDao     suggestionDao     = factory.getSuggestionDao();
    protected static RecipientDao      recipientDao      = factory.getRecipientDao();
    protected static QuestionDao       questionDao       = factory.getQuestionDao();
    protected static QuestMessageDao   questMessageDao   = factory.getQuestMessageDao();
    protected static EventDao          eventDao          = factory.getEventDao();
    protected static TaskDao           taskDao           = factory.getTaskDao();
    protected static CategoryDao       categoryDao       = factory.getCategoryDao();

    public abstract boolean execute() throws TelegramApiException, SQLException;

    protected int     sendMessageWithKeyboard(int messageId, ReplyKeyboard keyboard) throws TelegramApiException {
        return sendMessageWithKeyboard(getText(messageId), keyboard);
    }

    protected int     sendMessageWithKeyboard(String text, int keyboardId) throws TelegramApiException {
        return sendMessageWithKeyboard(text, keyboardMarkUpDao.select(keyboardId));
    }

    protected int     sendMessageWithKeyboard(String text, ReplyKeyboard keyboard) throws TelegramApiException {
        lastSendMessageID = sendMessageWithKeyboard(text, keyboard, chatId);
        return lastSendMessageID;
    }

    protected int     sendMessageWithKeyboard(String text, ReplyKeyboard keyboard, long chatId) throws TelegramApiException {
        return botUtils.sendMessageWithKeyboard(text, keyboard, chatId);
    }

    protected int     sendMessage(String text) throws TelegramApiException {
        return sendMessage(text, chatId);
    }

    protected int     sendMessage(String text, long chatId) throws TelegramApiException {
        return sendMessage(text, chatId, null);
    }

    protected int     sendMessage(String text, long chatId, Contact contact) throws TelegramApiException {
        lastSendMessageID = botUtils.sendMessage(text, chatId);
        if (contact != null) {
            botUtils.sendContact(chatId, contact);
        }
        return lastSendMessageID;
    }

    protected int     sendMessage(long messageId) throws TelegramApiException {
        return sendMessage(messageId, chatId);
    }

    protected int     sendMessage(long messageId, long chatId) throws TelegramApiException {
        return sendMessage(messageId, chatId, null);
    }

    protected int     sendMessage(long messageId, long chatId, Contact contact) throws TelegramApiException {
        return sendMessage(messageId, chatId, contact, null);
    }

    protected int     sendMessage(long messageId, long chatId, Contact contact, String photo) throws TelegramApiException {
        lastSendMessageID = botUtils.sendMessage(messageId, chatId, contact, photo);
        return lastSendMessageID;
    }

    protected int     toDeleteKeyboard(int messageDeleteId) {
        SetDeleteMessages.addKeyboard(chatId, messageDeleteId);
        return messageDeleteId;
    }

    protected int     sendMessageWithKeyboardTest(String text, ReplyKeyboard keyboard, long chatID) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage().setParseMode(ParseMode.HTML).setChatId(chatID).setText(text).setReplyMarkup(keyboard);
        sendMessageTest(text, sendMessage);
        return lastSendMessageID;
    }

    protected int     toDeleteMessage(int messageDeleteId) {
        SetDeleteMessages.addKeyboard(chatId, messageDeleteId);
        return messageDeleteId;
    }

    public    void    clear() {
        update = null;
        bot = null;
    }

    protected void    deleteMessage(int messageId) {
        deleteMessage(chatId, messageId);
    }

    protected void    deleteMessage(long chatId, int messageId) {
        botUtils.deleteMessage(chatId, messageId);
    }

    private   void    sendMessageTest(String text, SendMessage sendMessage) throws TelegramApiException {
        try {
            lastSendMessageID = bot.execute(sendMessage).getMessageId();
        } catch (TelegramApiRequestException e) {
            if (e.getApiResponse().contains("Bad Request: can't parse entities")) {
                sendMessage.setParseMode(null);
                sendMessage.setText(text + next + "Wrong number");
                lastSendMessageID = bot.execute(sendMessage).getMessageId();
            } else throw e;
        }
    }

    protected void    sendMessageWithAddition() throws TelegramApiException {
        deleteMessage(updateMessageId);
        baliviya.com.github.MBO.entity.standart.Message message = messageDao.getMessage(messageId);
        sendMessage(messageId, chatId, null, message.getPhoto());
        try {
            if (message.getFile() != null) {
                switch (message.getFileType()) {
                    case audio:
                        bot.execute(new SendAudio().setAudio(message.getFile()).setChatId(chatId));
                    case video:
                        bot.execute(new SendVideo().setVideo(message.getFile()).setChatId(chatId));
                    case document:
                        bot.execute(new SendDocument().setChatId(chatId).setDocument(message.getFile()));
                }
            }
        } catch (TelegramApiException e) {
            log.error("Exception by send file for message " + messageId, e);
        }
    }

    protected String  getLinkForUser(long chatId, String userName) {
        return String.format("<a href = \"tg://user?id=%s\">%s</a>", chatId, userName);
    }

    protected String  getText(int messageIdFromDb) {
        return messageDao.getMessageText(messageIdFromDb);
    }

    protected String  getTextFromGroup(int messageIdFromDb) {
        return messageDao.getMessageTextFromGroup(messageIdFromDb);
    }

    public    boolean isInitNormal(Update update, DefaultAbsSender bot) {
        if (botUtils == null) {
            botUtils = new BotUtil(bot);
        }
        this.update = update;
        this.bot = bot;
        chatId = UpdateUtil.getChatId(update);
        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            updateMessage = callbackQuery.getMessage();
            updateMessageText = callbackQuery.getData();
            updateMessageId = updateMessage.getMessageId();
            editableTextOfMessage = callbackQuery.getMessage().getText();
        } else if (update.hasMessage()) {
            updateMessage = update.getMessage();
            updateMessageId = updateMessage.getMessageId();
            if (updateMessage.hasText()) {
                updateMessageText = updateMessage.getText();
            }
            if (updateMessage.hasPhoto()) {
                int size = update.getMessage().getPhoto().size();
                updateMessagePhoto = update.getMessage().getPhoto().get(size - 1).getFileId();
            } else {
                updateMessagePhoto = null;
            }
        }
        if (hasContact()) {
            updateMessagePhone = update.getMessage().getContact().getPhoneNumber();
        }
        if (markChange == null) {
            markChange = getTextFromGroup(Const.EDIT_BUTTON_ICON);
        }
        return false;
    }

    protected boolean isAdmin() {
        return adminDao.isAdmin(chatId);
    }

    protected boolean isMainAdmin() {
        return adminDao.isMainAdmin(chatId);
    }

    protected boolean hasContact() {
        return update.hasMessage() && update.getMessage().getContact() != null;
    }

    protected boolean isButton(int buttonId) {
        return updateMessageText.equals(buttonDao.getButtonText(buttonId));
    }

    protected boolean hasCallbackQuery() { return update.hasCallbackQuery();}

    protected boolean hasMessageText() {
        return update.hasMessage() && update.getMessage().hasText();
    }

    protected boolean hasPhoto() {
        return update.hasMessage() && update.getMessage().hasPhoto();
    }

    protected boolean hasDocument() {
        return update.hasMessage() && update.getMessage().hasDocument();
    }

    protected boolean hasAudio() {
        return update.hasMessage() && update.getMessage().getAudio() != null;
    }

    protected boolean hasVideo() {
        return update.hasMessage() && update.getMessage().getVideo() != null;
    }

    protected void    deleteKeyboard() throws TelegramApiException {
        int deleteMessageId = sendMessageWithKeyboard("...", Const.MAIN_MENU_KEYBOARD);
        deleteMessage(deleteMessageId);
    }

    protected boolean hasContactOwner() {
        if (update.hasMessage() && update.getMessage().getContact() != null) {
            return Objects.equals(update.getMessage().getFrom().getId(), update.getMessage().getContact().getUserID());
        }
        return false;
    }

    protected String getItalicsText(String text) {
        return "<i>" + text + "</i>";
    }

    protected String getBoldText(String text) {
        return "<b>" + text + "</b>";
    }
}
