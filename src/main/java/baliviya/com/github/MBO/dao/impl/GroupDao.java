package baliviya.com.github.MBO.dao.impl;

import baliviya.com.github.MBO.dao.AbstractDao;
import baliviya.com.github.MBO.entity.custom.Group;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class GroupDao extends AbstractDao<Group> {

    public List<Group> getGroups() {
        sql = "SELECT * FROM GROUPS ORDER BY ID";
        return getJdbcTemplate().query(sql, this::mapper);
    }

    public boolean isExist(long chatId) {
        sql = "SELECT COUNT(CHAT_ID) FROM GROUPS WHERE CHAT_ID = ?";
        return getJdbcTemplate().queryForObject(sql, setParam(chatId), Integer.class) > 0;
    }

    public void update(Group group) {
        sql = "UPDATE GROUPS SET NAMES = ?, CHAT_ID = ?, USER_NAME = ?, IS_REGISTERED = ?, MESSAGE = ?, IS_CAN_PHOTO = ?, IS_CAN_VIDEO = ?, IS_CAN_AUDIO = ?, IS_CAN_FILE = ?, IS_CAN_LINK = ?, IS_CAN_STICKER = ?, IS_CAN_WITHOUT_TAG = ? WHERE CHAT_ID = ?";
        getJdbcTemplate().update(sql, group.getNames(), group.getChatId(), group.getUserName(), group.isRegistered(), group.getMessage(), group.isCanPhoto(), group.isCanVideo(), group.isCanAudio(),
                group.isCanFile(), group.isCanLink(), group.isCanSticker(), group.isCanWithoutTag(), group.getChatId());
    }

    public void insert(Group group) {
        sql = "INSERT INTO GROUPS(NAMES, CHAT_ID, USER_NAME, IS_REGISTERED) VALUES ( ?,?,?, DEFAULT )";
        getJdbcTemplate().update(sql, group.getNames(), group.getChatId(), group.getUserName());
    }

    public List<Group> getGroupsOrder() {
        sql = "SELECT * FROM GROUPS ORDER BY IS_REGISTERED DESC, ID";
        return getJdbcTemplate().query(sql, this::mapper);
    }

    public Group getGroupToId(int id) {
        sql = "SELECT * FROM GROUPS WHERE ID = ?";
        return getJdbcTemplate().queryForObject(sql, setParam(id), this::mapper);
    }

    @Override
    protected Group mapper(ResultSet rs, int index) throws SQLException {
        Group group = new Group();
        group.setId(rs.getInt(1));
        group.setNames(rs.getString(2));
        group.setChatId(rs.getLong(3));
        group.setUserName(rs.getString(4));
        group.setRegistered(rs.getBoolean(5));
        group.setMessage(rs.getString(6));
        group.setCanPhoto(rs.getBoolean(7));
        group.setCanVideo(rs.getBoolean(8));
        group.setCanAudio(rs.getBoolean(9));
        group.setCanFile(rs.getBoolean(10));
        group.setCanLink(rs.getBoolean(11));
        group.setCanSticker(rs.getBoolean(12));
        group.setCanWithoutTag(rs.getBoolean(13));
        return group;
    }
}
