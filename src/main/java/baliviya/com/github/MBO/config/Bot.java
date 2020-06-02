package baliviya.com.github.MBO.config;

import baliviya.com.github.MBO.dao.DaoFactory;
import baliviya.com.github.MBO.dao.impl.PropertiesDao;
import baliviya.com.github.MBO.utils.Const;
import baliviya.com.github.MBO.utils.UpdateUtil;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class Bot extends TelegramLongPollingBot {

    private DaoFactory daoFactory = DaoFactory.getInstance();
    private PropertiesDao propertiesDao = daoFactory.getPropertiesDao();
    private String tokenBot;
    private String nameBot;
    private Map<Long, Conversation> conversations = new HashMap<>();

    public void onUpdateReceived(Update update) {
        Conversation conversation = getConversation(update);
        try {
            conversation.handleUpdate(update, this);
        } catch (Exception e) {
            e.printStackTrace();
//            log.error("Error №" + e);
        }
    }

    private Conversation getConversation(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        Conversation conversation = conversations.get(chatId);
        if (conversation == null) {
            log.info("InitNormal new conversation for '{}'", chatId);
            conversation = new Conversation();
            conversations.put(chatId, conversation);
        }
        return conversation;
    }

    public String getBotUsername() {
        if (nameBot == null || nameBot.isEmpty()) {
            nameBot = propertiesDao.getPropertiesValue(Const.BOT_NAME);
        }
        return nameBot;
    }

    public String getBotToken() {
        if (tokenBot == null || tokenBot.isEmpty()) {
            tokenBot = propertiesDao.getPropertiesValue(Const.BOT_TOKEN);
        }
        return tokenBot;
    }
}
