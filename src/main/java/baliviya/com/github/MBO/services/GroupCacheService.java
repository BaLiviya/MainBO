package baliviya.com.github.MBO.services;

import baliviya.com.github.MBO.dao.DaoFactory;
import baliviya.com.github.MBO.dao.impl.GroupDao;
import baliviya.com.github.MBO.entity.custom.Group;
import baliviya.com.github.MBO.utils.UpdateUtil;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class GroupCacheService {

    private static HashMap<Long, Group> groupsMap;
    private static GroupDao groupDao = DaoFactory.getInstance().getGroupDao();

    static {
        groupsMap = new HashMap<>();
        for (Group group : groupDao.getGroups()) {
            groupsMap.put(group.getChatId(), group);
        }
    }

    public static Group checkGroup(Update update, long chatId) throws SQLException, TelegramApiException {
        Group group = groupsMap.get(chatId);
        Chat chat = UpdateUtil.getChat(update);
        if (group == null) {
            Group groupEntity = new Group();
            groupEntity.setChatId(chatId);
            groupEntity.setNames(chat.getTitle());
            groupEntity.setUserName(chat.getUserName());
            groupEntity.setRegistered(false);
            save(groupEntity);
            groupsMap.put(chatId, groupEntity);
        } else if (group.getUserName() == null
                || !group.getUserName().equals(chat.getUserName())
                || !group.getNames().equals(chat.getTitle())) {
            group.setChatId(chatId);
            group.setNames(chat.getTitle());
            group.setUserName(chat.getUserName());
            group.setRegistered(group.isRegistered());
            save(group);
        }
        return groupsMap.get(chatId);
    }

    public static Group get(long chatIdGroup) {
        return groupsMap.getOrDefault(chatIdGroup, null);
    }

    public static List<Group> getAll() {
        return groupsMap.entrySet().stream().map(Map.Entry::getValue).collect(Collectors.toList())
                .stream().sorted((g1, g2) -> -Boolean.compare(g1.isRegistered(), g2.isRegistered())).collect(Collectors.toList());
    }

    public static void save(Group groupEntity) {
        log.info("Change group: {}", groupEntity);
        if (groupDao.isExist(groupEntity.getChatId())) {
            groupDao.update(groupEntity);
        } else {
            groupDao.insert(groupEntity);
        }
    }
}
