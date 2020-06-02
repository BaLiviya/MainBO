package baliviya.com.github.MBO.dao.impl;


import baliviya.com.github.MBO.dao.AbstractDao;
import baliviya.com.github.MBO.entity.standart.Admin;
import baliviya.com.github.MBO.utils.Const;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class AdminDao extends AbstractDao<Admin> {

    public boolean isAdmin(long chatId) {
        sql = "SELECT count(*) FROM " + Const.TABLE_NAME + ".ADMIN WHERE USER_ID = ?";
        int count  = getJdbcTemplate().queryForObject(sql, setParam(chatId), Integer.class);
        if (count > 0) {
            return true;
        }
        return false;
    }

    public boolean isMainAdmin(long chatId) {
        sql = "SELECT count(*) FROM ADMIN WHERE USER_ID = ?";
        int count  = getJdbcTemplate().queryForObject(sql, setParam(chatId), Integer.class);
        if (count > 0) {
            return true;
        }
        return false;
    }

    public List<Long> getAll() {
        sql = "SELECT USER_ID FROM " + Const.TABLE_NAME + ".ADMIN ORDER BY ID";
       return getJdbcTemplate().queryForList(sql, Long.class);
    }

    public void addAssistant(long chatId, String comment) {
        sql = "INSERT INTO " + Const.TABLE_NAME + ".ADMIN VALUES (DEFAULT, ?,?)";
        getJdbcTemplate().update(sql, chatId, comment);
    }

    public void delete(long chatId) {
        sql = "DELETE FROM " + Const.TABLE_NAME + ".ADMIN WHERE USER_ID = ?";
        getJdbcTemplate().update(sql, chatId);
    }

    @Override
    protected Admin mapper(ResultSet rs, int index) throws SQLException {
        Admin admin = new Admin();
        admin.setId(rs.getInt(1));
        admin.setUserId(rs.getLong(2));
        admin.setComment(rs.getString(3));
        return admin;
    }
}
