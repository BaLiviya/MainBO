package baliviya.com.github.MBO.dao.impl;

import baliviya.com.github.MBO.dao.AbstractDao;
import baliviya.com.github.MBO.entity.custom.HandlingName;
import baliviya.com.github.MBO.utils.Const;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class HandlingNameDao extends AbstractDao<HandlingName> {

    public List<HandlingName> getAllTraining() {
        sql = "SELECT * FROM " + Const.TABLE_NAME + ".TRAINING_NAME" ;
        return getJdbcTemplate().query(sql, this::mapper);
    }

    public List<HandlingName> getAllBusiness() {
        sql = "SELECT * FROM " + Const.TABLE_NAME + ".BUSINESS_NAME" ;
        return getJdbcTemplate().query(sql, this::mapper);
    }

    @Override
    protected HandlingName mapper(ResultSet rs, int index) throws SQLException {
        HandlingName handlingName = new HandlingName();
        handlingName.setId(rs.getInt(1));
        handlingName.setName(rs.getString(2));
        return handlingName;
    }
}
