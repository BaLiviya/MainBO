package baliviya.com.github.MBO.dao.impl;

import baliviya.com.github.MBO.dao.AbstractDao;
import baliviya.com.github.MBO.entity.custom.Handling;
import baliviya.com.github.MBO.utils.Const;

import java.sql.ResultSet;
import java.sql.SQLException;

public class HandlingDao extends AbstractDao<Handling> {

    public Handling getCourse(int courseNameId) {
        sql = "SELECT * FROM " + Const.TABLE_NAME + ".COURSE WHERE COURSE_NAME_ID = ?";
        return getJdbcTemplate().queryForObject(sql, setParam(courseNameId), this::mapper);
    }

    public Handling getTraining(int trainingNameId) {
        sql = "SELECT * FROM " + Const.TABLE_NAME + ".TRAINING WHERE TRAINING_NAME_ID = ?";
        return getJdbcTemplate().queryForObject(sql, setParam(trainingNameId), this::mapper);
    }

    public Handling getBusiness(int businessNameId) {
        sql = "SELECT * FROM " + Const.TABLE_NAME + ".BUSINESS WHERE BUSINESS_NAME_ID = ?";
        return getJdbcTemplate().queryForObject(sql, setParam(businessNameId), this::mapper);
    }

    @Override
    protected Handling mapper(ResultSet rs, int index) throws SQLException {
        Handling handling = new Handling();
        handling.setId(rs.getInt(1));
        handling.setPhoto(rs.getString(2));
        handling.setText(rs.getString(3));
        handling.setIdCoursesName(rs.getInt(4));
        return handling;
    }
}
