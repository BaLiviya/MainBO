package baliviya.com.github.MBO.dao.impl;

import baliviya.com.github.MBO.dao.AbstractDao;
import baliviya.com.github.MBO.entity.custom.QuestMessage;
import baliviya.com.github.MBO.entity.enums.Language;
import baliviya.com.github.MBO.utils.Const;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class QuestMessageDao extends AbstractDao<QuestMessage> {

    public List<QuestMessage> getAll(int questId, Language language) {
        sql = "SELECT * FROM " + Const.TABLE_NAME + ".QUEST_MESSAGE WHERE ID_QUEST = ? AND ID_LANG = ? ORDER BY ID";
        return getJdbcTemplate().query(sql, setParam(questId, language.getId()), this::mapper);
    }

    public void insert(QuestMessage questMessage) {
        sql = "INSERT INTO " + Const.TABLE_NAME + ".QUEST_MESSAGE (ID, ID_LANG, MESSAGE, RANGE, ID_QUEST) VALUES ( ?,?,?,?,? )";
        getJdbcTemplate().update(sql, setParam(questMessage.getId(), questMessage.getLanguageId(), questMessage.getMessage(), questMessage.getRange(), questMessage.getIdQuest()));
    }

    public QuestMessage getById(int id, Language language) {
        sql = "SELECT * FROM " + Const.TABLE_NAME + ".QUEST_MESSAGE WHERE ID = ? AND ID_LANG = ?";
        return getJdbcTemplate().queryForObject(sql, setParam(id, language.getId()), this::mapper);
    }

    public void update(QuestMessage questMessage) {
        sql = "UPDATE " + Const.TABLE_NAME + ".QUEST_MESSAGE SET MESSAGE = ?, RANGE = ? WHERE ID = ? AND ID_LANG = ?";
        getJdbcTemplate().update(sql, questMessage.getMessage(), questMessage.getRange(), questMessage.getId(), questMessage.getLanguageId());
    }

    public void delete(int id) {
        sql = "DELETE FROM " + Const.TABLE_NAME + ".QUEST_MESSAGE WHERE ID = ?";
        getJdbcTemplate().update(sql, id);
    }

    public void deleteByQuestId(int questId) {
        sql = "DELETE FROM " + Const.TABLE_NAME + ".QUEST_MESSAGE WHERE ID_QUEST = ?";
        getJdbcTemplate().update(sql, questId);
    }

    @Override
    protected QuestMessage mapper(ResultSet rs, int index) throws SQLException {
        QuestMessage questMessage = new QuestMessage();
        questMessage.setId(rs.getInt(1));
        questMessage.setLanguageId(rs.getInt(2));
        questMessage.setMessage(rs.getString(3));
        questMessage.setRange(rs.getString(4));
        questMessage.setIdQuest(rs.getInt(5));
        return questMessage;
    }
}
