package baliviya.com.github.MBO.dao.impl;

import baliviya.com.github.MBO.dao.AbstractDao;
import baliviya.com.github.MBO.entity.custom.ServiceType;
import baliviya.com.github.MBO.utils.Const;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ServiceTypeDao extends AbstractDao<ServiceType> {

    public List<ServiceType> getAll() {
        sql = "SELECT * FROM " + Const.TABLE_NAME + ".SERVICE_TYPE";
        return getJdbcTemplate().query(sql, this::mapper);
    }

    @Override
    protected ServiceType mapper(ResultSet rs, int index) throws SQLException {
        ServiceType serviceType = new ServiceType();
        serviceType.setId(rs.getInt(1));
        serviceType.setName(rs.getString(2));
        return serviceType;
    }
}
