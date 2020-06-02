package baliviya.com.github.MBO.dao.impl;

import baliviya.com.github.MBO.dao.AbstractDao;
import baliviya.com.github.MBO.entity.custom.CoursesName;
import baliviya.com.github.MBO.utils.Const;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class CoursesNameDao extends AbstractDao<CoursesName> {

    public List<CoursesName> getAll(int idCoursesType) {
        sql = "SELECT * FROM " + Const.TABLE_NAME + ".COURSES_NAME WHERE COURSES_TYPE_ID = ?";
        return getJdbcTemplate().query(sql, setParam(idCoursesType), this::mapper);
    }

    @Override
    protected CoursesName mapper(ResultSet rs, int index) throws SQLException {
        CoursesName coursesName = new CoursesName();
        coursesName.setId(rs.getInt(1));
        coursesName.setName(rs.getString(2));
        coursesName.setIdCoursesType(rs.getInt(3));
        return coursesName;
    }
}
