package baliviya.com.github.MBO.dao.impl;


import baliviya.com.github.MBO.dao.AbstractDao;
import baliviya.com.github.MBO.entity.custom.Suggestion;
import baliviya.com.github.MBO.utils.Const;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class SuggestionDao extends AbstractDao<Suggestion> {

    public void insert(Suggestion suggestion) {
        sql = "INSERT INTO " + Const.TABLE_NAME + ".SUGGESTION (FULL_NAME, PHONE_NUMBER, EMAIL, TEXT, POST_DATE) VALUES (?,?,?,?,?)";
        getJdbcTemplate().update(sql, setParam(
                suggestion.getFullName(), suggestion.getPhoneNumber(),
                suggestion.getEmail(), suggestion.getText(), suggestion.getPostDate()));
    }

    public void insertComplaint(Suggestion suggestion) {
        sql = "INSERT INTO " + Const.TABLE_NAME + ".COMPLAINT (FULL_NAME, PHONE_NUMBER, EMAIL, TEXT, POST_DATE) VALUES (?,?,?,?,?)";
        getJdbcTemplate().update(sql, setParam(
                suggestion.getFullName(), suggestion.getPhoneNumber(),
                suggestion.getEmail(), suggestion.getText(), suggestion.getPostDate()));
    }

    public int getCount() {
        sql = "SELECT COUNT(ID) FROM " + Const.TABLE_NAME + ".SUGGESTION";
        return getJdbcTemplate().queryForObject(sql, Integer.class);
    }

    public List<Suggestion> getSuggestionsByTime(Date dateBegin, Date deadline) {
        sql = "SELECT * FROM " + Const.TABLE_NAME + ".SUGGESTION WHERE POST_DATE BETWEEN to_date(?, 'YYYY-MM-DD') AND to_date(?, 'YYYY-MM-DD') ORDER BY ID";
        return getJdbcTemplate().query(sql, setParam(dateBegin, deadline), this::mapper);
    }

    public List<Suggestion> getComplaintsByTime(Date dateBegin, Date deadline) {
        sql = "SELECT * FROM " + Const.TABLE_NAME + ".COMPLAINT WHERE POST_DATE BETWEEN to_date(?, 'YYYY-MM-DD') AND to_date(?, 'YYYY-MM-DD') ORDER BY ID";
        return getJdbcTemplate().query(sql, setParam(dateBegin, deadline), this::mapper);
    }

    @Override
    protected Suggestion mapper(ResultSet rs, int index) throws SQLException {
        Suggestion entity = new Suggestion();
        entity.setId(rs.getInt(1));
        entity.setFullName(rs.getString(2));
        entity.setPhoneNumber(rs.getString(3));
        entity.setEmail(rs.getString(4));
        entity.setText(rs.getString(5));
        entity.setPostDate(rs.getDate(6));
        return entity;
    }
}