package baliviya.com.github.MBO.dao.impl;

import baliviya.com.github.MBO.dao.AbstractDao;
import baliviya.com.github.MBO.entity.custom.SurveyAnswer;
import baliviya.com.github.MBO.utils.Const;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class SurveyAnswerDao extends AbstractDao<SurveyAnswer> {

    public void insert(SurveyAnswer surveyAnswer) {
        int id = getNextId("SURVEY_ANSWERS");
        sql = "INSERT INTO " + Const.TABLE_NAME + ".SURVEY_ANSWERS (ID, SURVEY_ID, CHAT_ID, BUTTON, TEXT) VALUES ( ?,?,?,?,? )";
        getJdbcTemplate().update(sql, setParam(id, surveyAnswer.getSurveyId(), surveyAnswer.getChatId(), surveyAnswer.getButton(), surveyAnswer.getText()));
        surveyAnswer.setId(id);
    }

    public void update(int id, String text) {
        sql = "UPDATE " + Const.TABLE_NAME + ".SURVEY_ANSWERS SET TEXT = ? WHERE ID = ?";
        getJdbcTemplate().update(sql, text, id);
    }

    public void deleteByQuestId(int questId) {
        sql = "DELETE FROM " + Const.TABLE_NAME + ".SURVEY_ANSWERS WHERE SURVEY_ID = ?";
        getJdbcTemplate().update(sql, questId);
    }

    public List<SurveyAnswer> getAll(int surveyId) {
        sql = "SELECT * FROM " + Const.TABLE_NAME + ".SURVEY_ANSWERS WHERE SURVEY_ID = ? ORDER BY ID DESC";
        return getJdbcTemplate().query(sql, setParam(surveyId), this::mapper);
    }

    @Override
    protected SurveyAnswer mapper(ResultSet rs, int index) throws SQLException {
        SurveyAnswer surveyAnswer = new SurveyAnswer();
        surveyAnswer.setId(rs.getInt(1));
        surveyAnswer.setSurveyId(rs.getInt(2));
        surveyAnswer.setChatId(rs.getLong(3));
        surveyAnswer.setButton(rs.getString(4));
        surveyAnswer.setText(rs.getString(5));
        return surveyAnswer;
    }
}
