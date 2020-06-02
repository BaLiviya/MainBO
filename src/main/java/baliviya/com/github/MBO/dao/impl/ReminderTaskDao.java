package baliviya.com.github.MBO.dao.impl;

import baliviya.com.github.MBO.dao.AbstractDao;
import baliviya.com.github.MBO.entity.custom.ReminderTask;
import baliviya.com.github.MBO.utils.Const;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class ReminderTaskDao extends AbstractDao<ReminderTask> {

    public ReminderTask       getById(int reminderTaskId, String tableName) {
        sql = "SELECT * FROM " + tableName + ".REMINDER_TASK WHERE ID = ?";
        return getJdbcTemplate().queryForObject(sql, setParam(reminderTaskId), this::mapper);
    }

    public List<ReminderTask> getAll(String tableName) {
        sql = "SELECT * FROM " + tableName + ".REMINDER_TASK";
        return getJdbcTemplate().query(sql, this::mapper);
    }

    public void               update(ReminderTask reminderTask, String tableName) {
        sql = "UPDATE " + tableName + ".REMINDER_TASK SET DATE_BEGIN = ?, TEXT = ? WHERE ID = ?";
        getJdbcTemplate().update(sql, reminderTask.getDateBegin(),reminderTask.getText(), reminderTask.getId());
    }

    public void               insert(ReminderTask reminderTask, String tableName) {
        sql = "INSERT INTO " + tableName + ".REMINDER_TASK(TEXT, DATE_BEGIN) VALUES ( ?,? )";
        getJdbcTemplate().update(sql, reminderTask.getText(), reminderTask.getDateBegin());
    }

    public void               delete(int reminderTaskId, String tableName) {
        sql = "DELETE FROM " + tableName + ".REMINDER_TASK WHERE ID = ?";
        getJdbcTemplate().update(sql, setParam(reminderTaskId));
    }

    public List<ReminderTask> getByTime(Date dateBegin, Date dateEnd) {
        sql = "SELECT * FROM " + Const.TABLE_NAME + ".REMINDER_TASK WHERE DATE_BEGIN BETWEEN to_date(?,'YYYY-MM-DD') AND to_date(?,'YYYY-MM-DD HH:mm:SS')";
        return getJdbcTemplate().query(sql, setParam(dateBegin, dateEnd), this::mapper);
    }

    public List<ReminderTask> getAll() {
        sql = "SELECT * FROM " + Const.TABLE_NAME + ".REMINDER_TASK";
        return getJdbcTemplate().query(sql, this::mapper);
    }

    public void               delete(int reminderTaskId) {
        sql = "DELETE FROM " + Const.TABLE_NAME + ".REMINDER_TASK WHERE ID = ?";
        getJdbcTemplate().update(sql, setParam(reminderTaskId));
    }

    @Override
    protected ReminderTask mapper(ResultSet rs, int index) throws SQLException {
        ReminderTask reminderTask = new ReminderTask();
        reminderTask.setId(rs.getInt(1));
        reminderTask.setText(rs.getString(2));
        reminderTask.setDateBegin(rs.getDate(3));
        return reminderTask;
    }
}
