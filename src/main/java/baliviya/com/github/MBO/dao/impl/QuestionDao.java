package baliviya.com.github.MBO.dao.impl;

import baliviya.com.github.MBO.dao.AbstractDao;
import baliviya.com.github.MBO.entity.custom.Question;
import baliviya.com.github.MBO.entity.enums.Language;
import baliviya.com.github.MBO.utils.Const;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class QuestionDao extends AbstractDao<Question> {

    public List<Question> getAllActive(Language language, long chatId) {
        sql = "SELECT * FROM " + Const.TABLE_NAME + ".QUESTION WHERE IS_HIDE = FALSE AND LANG_ID = ? AND ID NOT IN (SELECT SURVEY_ID FROM " + Const.TABLE_NAME + ".SURVEY_ANSWERS WHERE CHAT_ID = ?) ORDER BY ID";
        return getJdbcTemplate().query(sql, setParam(language.getId(), chatId), this::mapper);
    }

    public void insert(Question question) {
        sql = "INSERT INTO " + Const.TABLE_NAME + ".QUESTION (ID, NAME, DESC, LANG_ID, IS_HIDE) VALUES ( ?,?,?,?,? )";
        getJdbcTemplate().update(sql, setParam(question.getId(), question.getName(), question.getDesc(), question.getLanguageId(), question.isHide()));
    }

    public List<Question> getAll(Language language) {
        sql = "SELECT * FROM " + Const.TABLE_NAME + ".QUESTION WHERE LANG_ID = ? ORDER BY ID DESC";
        return getJdbcTemplate().query(sql, setParam(language.getId()), this::mapper);
    }

    public Question getById(int id, Language language) {
        sql = "SELECT * FROM " + Const.TABLE_NAME + ".QUESTION WHERE ID = ? AND LANG_ID = ?";
        return getJdbcTemplate().queryForObject(sql, setParam(id, language.getId()), this::mapper);
    }

    public void update(Question question) {
        sql = "UPDATE " + Const.TABLE_NAME + ".QUESTION SET NAME = ?, DESC = ? WHERE ID = ? AND LANG_ID = ?";
        getJdbcTemplate().update(sql, question.getName(), question.getDesc(), question.getId(), question.getLanguageId());
    }

    public void delete(int questId) {
        sql = "DELETE FROM " + Const.TABLE_NAME + ".QUESTION WHERE ID = ?";
        getJdbcTemplate().update(sql, questId);
    }

    @Override
    protected Question mapper(ResultSet rs, int index) throws SQLException {
        Question question = new Question();
        question.setId(rs.getInt(1));
        question.setName(rs.getString(2));
        question.setDesc(rs.getString(3));
        question.setLanguageId(rs.getInt(4));
        question.setHide(rs.getBoolean(5));
        return question;
    }
}
