package baliviya.com.github.MBO.dao.impl;

import baliviya.com.github.MBO.dao.AbstractDao;
import baliviya.com.github.MBO.entity.custom.RegistrationHandling;
import baliviya.com.github.MBO.utils.Const;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class RegistrationHandlingDao extends AbstractDao<RegistrationHandling> {

    public void insertCourse(RegistrationHandling course) {
        sql = "INSERT INTO " + Const.TABLE_NAME + ".REGISTRATION_COURSES (CHAT_ID, IIN, COURSE_ID, REGISTRATION_DATE, IS_COME) VALUES ( ?,?,?,?,? )";
        getJdbcTemplate().update(sql, setParam(course.getChatId(), course.getIin(), course.getIdHandling(), course.getRegistrationDate(), course.isCome()));
    }

    public void insertTraining(RegistrationHandling training) {
        sql = "INSERT INTO " + Const.TABLE_NAME + ".REGISTRATION_TRAINING (CHAT_ID, IIN, TRAINING_ID, REGISTRATION_DATE, IS_COME) VALUES ( ?,?,?,?,? )";
        getJdbcTemplate().update(sql, setParam(training.getChatId(), training.getIin(), training.getIdHandling(), training.getRegistrationDate(), training.isCome()));
    }

    public void insertBusiness(RegistrationHandling business) {
        sql = "INSERT INTO " + Const.TABLE_NAME + ".REGISTRATION_BUSINESS (CHAT_ID, IIN, BUSINESS_ID, REGISTRATION_DATE, IS_COME) VALUES ( ?,?,?,?,? )";
        getJdbcTemplate().update(sql, setParam(business.getChatId(), business.getIin(), business.getIdHandling(), business.getRegistrationDate(), business.isCome()));
    }

    public List<RegistrationHandling> getAllCoursesByTime(Date dateBegin, Date dateEnd) {
        sql = "SELECT * FROM " + Const.TABLE_NAME + ".REGISTRATION_COURSES WHERE REGISTRATION_DATE BETWEEN to_date(?,'YYYY-MM-DD') AND to_date(?,'YYYY-MM-DD') ORDER BY ID";
//        sql = "SELECT CHAT_ID, count(ID) AS total FROM " + Const.TABLE_NAME + ".REGISTRATION_COURSES WHERE REGISTRATION_DATE BETWEEN ? AND ? GROUP BY CHAT_ID ORDER BY total DESC";
        return getJdbcTemplate().query(sql, setParam(dateBegin, dateEnd), this::mapper);
    }

    @Override
    protected RegistrationHandling mapper(ResultSet rs, int index) throws SQLException {
        RegistrationHandling registrationHandling = new RegistrationHandling();
        registrationHandling.setId(rs.getInt(1));
        registrationHandling.setChatId(rs.getLong(2));
        registrationHandling.setIin(rs.getLong(3));
        registrationHandling.setIdHandling(rs.getInt(4));
        registrationHandling.setRegistrationDate(rs.getDate(5));
        registrationHandling.setCome(rs.getBoolean(6));
        return registrationHandling;
    }
}
