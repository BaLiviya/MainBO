package baliviya.com.github.MBO.dao;

import baliviya.com.github.MBO.dao.impl.*;
import baliviya.com.github.MBO.utils.PropertiesUtil;
import lombok.NoArgsConstructor;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@NoArgsConstructor
public class DaoFactory {

    private static DataSource source;
    private static DaoFactory daoFactory = new DaoFactory();

    public  static DaoFactory               getInstance() { return daoFactory; }

    public  static DataSource               getDataSource() {
        if (source == null) source = getDriverManagerDataSource();
        return source;
    }

    private static DriverManagerDataSource  getDriverManagerDataSource() {
        DriverManagerDataSource driver = new DriverManagerDataSource();
        driver.setDriverClassName(PropertiesUtil.getProperty("jdbc.driverClassName"));
        driver.setUrl(PropertiesUtil.getProperty("jdbc.url"));
        driver.setUsername(PropertiesUtil.getProperty("jdbc.username"));
        driver.setPassword(PropertiesUtil.getProperty("jdbc.password"));
        return driver;
    }

    public PropertiesDao                    getPropertiesDao()              { return new PropertiesDao(); }

    public LanguageUserDao                  getLanguageUserDao()            { return new LanguageUserDao(); }

    public ButtonDao                        getButtonDao()                  { return new ButtonDao(); }

    public MessageDao                       getMessageDao()                 { return new MessageDao(); }

    public KeyboardMarkUpDao                getKeyboardMarkUpDao()          { return new KeyboardMarkUpDao(); }

    public UserDao                          getUserDao()                    { return new UserDao(); }

    public AdminDao                         getAdminDao()                   { return new AdminDao(); }

    public SuggestionDao                    getSuggestionDao()              { return new SuggestionDao(); }

    public RecipientDao                     getRecipientDao()               { return new RecipientDao(); }

    public CoursesTypeDao                   getCoursesTypeDao()             { return new CoursesTypeDao(); }

    public CoursesNameDao                   getCoursesNameDao()             { return new CoursesNameDao(); }

    public HandlingDao                      getHandlingDao()                { return new HandlingDao(); }

    public RegistrationHandlingDao          getRegistrationHandlingDao()    { return new RegistrationHandlingDao(); }

    public HandlingNameDao                  getHandlingNameDao()            { return new HandlingNameDao(); }

    public ServiceTypeDao                   getServiceTypeDao()             { return new ServiceTypeDao(); }

    public ServiceDao                       getServiceDao()                 { return new ServiceDao(); }

    public RegistrationServiceDao           getRegistrationServiceDao()     { return new RegistrationServiceDao(); }

    public EventDao                         getEventDao()                   { return new EventDao(); }

    public QuestionDao                      getQuestionDao()                { return new QuestionDao(); }

    public QuestMessageDao                  getQuestMessageDao()            { return new QuestMessageDao(); }

    public SurveyAnswerDao                  getSurveyAnswerDao()            { return new SurveyAnswerDao(); }

    public GroupDao                         getGroupDao()                   { return new GroupDao(); }

    public DonateDao                        getDonateDao()                  { return new DonateDao(); }

    public ReceiverDao                      getReceiverDao()                { return new ReceiverDao(); }

    public TaskDao                          getTaskDao()                    { return new TaskDao(); }

    public CategoryDao                      getCategoryDao()                { return new CategoryDao();}

    public EmployeeDao                      getEmployeeDao()                { return new EmployeeDao(); }

    public TaskArchiveDao                   getTaskArchiveDao()             { return new TaskArchiveDao(); }

    public ReminderTaskDao                  getReminderTaskDao()            { return new ReminderTaskDao(); }
}
