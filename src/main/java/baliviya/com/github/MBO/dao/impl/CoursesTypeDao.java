package baliviya.com.github.MBO.dao.impl;

import baliviya.com.github.MBO.dao.AbstractDao;
import baliviya.com.github.MBO.entity.custom.CoursesType;
import baliviya.com.github.MBO.utils.Const;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class CoursesTypeDao extends AbstractDao<CoursesType> {

    public List<CoursesType> getAll() {
        sql = "SELECT * FROM " + Const.TABLE_NAME + ".COURSES_TYPE";
        return getJdbcTemplate().query(sql, this::mapper);
    }

    @Override
    protected CoursesType mapper(ResultSet rs, int index) throws SQLException {
        CoursesType coursesType = new CoursesType();
        coursesType.setId(rs.getInt(1));
        coursesType.setName(rs.getString(2));
        return coursesType;
    }
}
