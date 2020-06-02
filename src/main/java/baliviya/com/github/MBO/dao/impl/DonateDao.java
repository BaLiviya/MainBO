package baliviya.com.github.MBO.dao.impl;

import baliviya.com.github.MBO.dao.AbstractDao;
import baliviya.com.github.MBO.entity.custom.Donate;
import baliviya.com.github.MBO.utils.Const;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class DonateDao extends AbstractDao<Donate> {

    public void insert(Donate donate) {
        sql = "INSERT INTO DONATE (DATE, CHARITY, PEOPLE_TYPE, DONATE_TYPE, CHAT_ID, DISTRICT, COMPANY_INFO) VALUES ( ?,?,?,?,?,?,? )";
        getJdbcTemplate().update(sql, donate.getDate(), donate.getCharity(), donate.getPeopleType(), donate.getDonateType(), donate.getChatId(), donate.getDistrict(), donate.getCompanyInfo());
    }

    public List<Donate> getAll() {
        sql = "SELECT * FROM " + Const.TABLE_NAME + ".DONATE";
        return getJdbcTemplate().query(sql, this::mapper);
    }

    @Override
    protected Donate mapper(ResultSet rs, int index) throws SQLException {
        Donate donate = new Donate();
        donate.setId(rs.getInt(1));
        donate.setDate(rs.getDate(2));
        donate.setCharity(rs.getString(3));
        donate.setPeopleType(rs.getString(4));
        donate.setDonateType(rs.getString(5));
        donate.setChatId(rs.getLong(6));
        donate.setDistrict(rs.getString(7));
        donate.setCompanyInfo(rs.getString(8));
        return donate;
    }
}
