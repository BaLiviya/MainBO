package baliviya.com.github.MBO.dao.impl;

import baliviya.com.github.MBO.dao.AbstractDao;
import baliviya.com.github.MBO.entity.custom.Service;
import baliviya.com.github.MBO.utils.Const;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ServiceDao extends AbstractDao<Service> {

    public List<Service> getAll(int serviceTypeId) {
        sql = "SELECT * FROM " + Const.TABLE_NAME + ".SERVICE WHERE SERVICE_TYPE_ID = ?";
        return getJdbcTemplate().query(sql, setParam(serviceTypeId), this::mapper);
    }

    @Override
    protected Service mapper(ResultSet rs, int index) throws SQLException {
        Service service = new Service();
        service.setId(rs.getInt(1));
        service.setPhoto(rs.getString(2));
        service.setText(rs.getString(3));
        service.setServiceTypeId(rs.getInt(4));
        service.setFullName(rs.getString(5));
        return service;
    }
}
