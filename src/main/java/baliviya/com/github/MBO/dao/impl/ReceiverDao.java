package baliviya.com.github.MBO.dao.impl;

import baliviya.com.github.MBO.dao.AbstractDao;
import baliviya.com.github.MBO.entity.custom.Receiver;
import baliviya.com.github.MBO.utils.Const;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ReceiverDao extends AbstractDao<Receiver> {

    public void insert(Receiver receiver) {
        sql = "INSERT INTO RECEIVER(DATE_REGISTER, CHARITY_TYPE, TEXT, CHAT_ID) VALUES ( ?,?,?,? )";
        getJdbcTemplate().update(sql, receiver.getDate(), receiver.getCharityType(), receiver.getText(), receiver.getChatId());
    }

    public List<Receiver> getAll() {
        sql = "SELECT * FROM " + Const.TABLE_NAME + ".RECEIVER";
        return getJdbcTemplate().query(sql, this::mapper);
    }

    @Override
    protected Receiver mapper(ResultSet rs, int index) throws SQLException {
        Receiver receiver = new Receiver();
        receiver.setId(rs.getInt(1));
        receiver.setDate(rs.getDate(2));
        receiver.setCharityType(rs.getString(3));
        receiver.setText(rs.getString(4));
        receiver.setChatId(rs.getLong(5));
        return receiver;
    }
}
