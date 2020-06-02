package baliviya.com.github.MBO.dao.impl;

import baliviya.com.github.MBO.dao.AbstractDao;
import baliviya.com.github.MBO.entity.custom.Category;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class CategoryDao extends AbstractDao<Category> {

    public List<Category> getList() {
        sql = "SELECT * FROM CATEGORY WHERE IS_HIDE = FALSE";
        return getJdbcTemplate().query(sql, this::mapper);
    }

    public Category getCategory(int categoryId) {
        sql = "SELECT * FROM CATEGORY WHERE ID = ?";
        return getJdbcTemplate().queryForObject(sql, setParam(categoryId), this::mapper);
    }

    @Override
    protected Category mapper(ResultSet rs, int index) throws SQLException {
        Category category = new Category();
        category.setId(rs.getInt(1));
        category.setName(rs.getString(2));
        category.setLanguage(rs.getBoolean(3));
        category.setHide(rs.getBoolean(4));
        return category;
    }
}
