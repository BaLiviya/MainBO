package baliviya.com.github.MBO.dao.impl;

import baliviya.com.github.MBO.dao.AbstractDao;
import baliviya.com.github.MBO.entity.custom.Task;
import baliviya.com.github.MBO.utils.components.DataTable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class TaskDao extends AbstractDao<Task> {

    public int getTaskCount(int statusId) {
        sql = "SELECT COUNT(ID) AS TOTAL FROM TASK WHERE ID_STATUS = ?";
        return getDBUtils().queryDataRec(sql, statusId).getInt("TOTAL");
    }

    public int getAllTaskCountCategory(int categoryId) {
        sql = "SELECT COUNT(ID) FROM TASK WHERE DISTRICT = ?";
        return getJdbcTemplate().queryForObject(sql, setParam(categoryId), Integer.class);
    }

    public int getTaskCountCategory(int categoryId, int statusId) {
        sql = "SELECT COUNT(ID) FROM TASK WHERE DISTRICT = ? AND ID_STATUS = ?";
        return getJdbcTemplate().queryForObject(sql, setParam(categoryId, statusId), Integer.class);
    }

    public DataTable getCountPeople(Date dateBegin, Date dateEnd) {
        sql = "SELECT PEOPLE_NAME, COUNT(ID) AS TOTAL FROM TASK WHERE DATE_BEGIN BETWEEN ? AND ? GROUP BY PEOPLE_NAME ORDER BY TOTAL DESC";
        return getDBUtils().query(sql, dateBegin, dateEnd);
    }

    public List<Task> getTaskToTime(int groupId, Date dateBegin, Date deadline) {
        sql = "SELECT * FROM TASK WHERE DATE_BEGIN BETWEEN to_date(?,'YYYY-MM-DD HH:mm:SS') AND to_date(?,'YYYY-MM-DD HH:mm:SS') AND GROUP_ID = ?";
        return getJdbcTemplate().query(sql, setParam(dateBegin, deadline, groupId), this::mapper);
    }

    public int getTaskCountTime(int groupId, int idStatus, Date dateBegin, Date deadline) {
        sql = "SELECT COUNT(ID) AS TOTAL FROM TASK WHERE DATE_BEGIN BETWEEN to_date(?,'YYYY-MM-DD HH:mm:SS') AND to_date(?,'YYYY-MM-DD HH:mm:SS') AND GROUP_ID = ? AND ID_STATUS = ?";
        return getDBUtils().queryDataRec(sql, dateBegin, deadline, groupId, idStatus).getInt("TOTAL");
    }

    @Override
    protected Task mapper(ResultSet rs, int index) throws SQLException {
        Task task = new Task();
        task.setId(rs.getInt(1));
        task.setStatusId(rs.getInt(2));
        task.setEmployeeId(rs.getLong(3));
        task.setTaskText(rs.getString(4));
        task.setDateBegin(rs.getDate(5));
        task.setPeopleId(rs.getLong(6));
        task.setPeopleName(rs.getString(7));
        task.setDistrict(rs.getInt(8));
        task.setGroups(rs.getInt(9));
        task.setMessageId(rs.getInt(10));
        return task;
    }
}
