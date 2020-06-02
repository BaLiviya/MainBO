package baliviya.com.github.MBO.dao.impl;

import baliviya.com.github.MBO.dao.AbstractDao;
import baliviya.com.github.MBO.entity.custom.TaskArchive;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class TaskArchiveDao extends AbstractDao<TaskArchive> {

    public List<TaskArchive> getTaskArchive(int taskId) {
        sql = "SELECT * FROM TASK_ARCHIVE WHERE TASK_ID = ?";
        return getJdbcTemplate().query(sql, setParam(taskId), this::mapper);
    }

    @Override
    protected TaskArchive mapper(ResultSet rs, int index) throws SQLException {
        TaskArchive taskArchive = new TaskArchive();
        taskArchive.setId(rs.getInt(1));
        taskArchive.setText(rs.getString(2));
        taskArchive.setTaskId(rs.getInt(3));
        taskArchive.setDate(rs.getDate(4));
        return taskArchive;
    }
}
