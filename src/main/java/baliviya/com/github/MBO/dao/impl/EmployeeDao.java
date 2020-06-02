package baliviya.com.github.MBO.dao.impl;

import baliviya.com.github.MBO.dao.AbstractDao;
import baliviya.com.github.MBO.entity.custom.Employee;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class EmployeeDao extends AbstractDao<Employee> {

    public List<Employee> getList(int categotyId) {
        sql = "SELECT * FROM EMPLOYEE WHERE ID_CATEGORY = ?";
        return getJdbcTemplate().query(sql, setParam(categotyId), this::mapper);
    }

    @Override
    protected Employee mapper(ResultSet rs, int index) throws SQLException {
        Employee employee = new Employee();
        employee.setId(rs.getInt(1));
        employee.setEmployeeId(rs.getLong(2));
        employee.setCategoryId(rs.getInt(3));
        return employee;
    }
}
