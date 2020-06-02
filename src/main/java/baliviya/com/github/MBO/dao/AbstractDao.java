package baliviya.com.github.MBO.dao;

import baliviya.com.github.MBO.config.Conversation;
import baliviya.com.github.MBO.entity.enums.Language;
import baliviya.com.github.MBO.services.LanguageService;
import baliviya.com.github.MBO.utils.Const;
import baliviya.com.github.MBO.utils.DataBaseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
public abstract class AbstractDao<T> {

    protected        String     sql;
    protected static DaoFactory factory = DaoFactory.getInstance();

    protected Object[] setParam(Object... args) {
        return args;
    }

    protected abstract T mapper(ResultSet rs, int index) throws SQLException;

    protected static JdbcTemplate getJdbcTemplate() {return new JdbcTemplate(getDataSource());}

    private static DataSource getDataSource() { return DaoFactory.getDataSource();}

    protected Language getLanguage() {
        if (getChatId() == 0) return Language.ru;
        return LanguageService.getLanguage(getChatId());
    }

    private long getChatId() {
        return Conversation.getCurrentChatId();
    }

    public int getNextId(String tableNames) {
        sql = "SELECT MAX(ID) FROM " + Const.TABLE_NAME + "." + tableNames;
        try {
            return getJdbcTemplate().queryForObject(sql, Integer.class) + 1;
        } catch (Exception e) {
            log.info("getNextId for {} has exception, return id = 1", tableNames);
            log.error("getNextId:", e);
            return 1;
        }
    }

    protected static DataBaseUtils getDBUtils() {
        return new DataBaseUtils(DaoFactory.getDataSource());
    }

}
