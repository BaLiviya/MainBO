package baliviya.com.github.MBO.dao.impl;

import baliviya.com.github.MBO.dao.AbstractDao;
import baliviya.com.github.MBO.entity.custom.Recipient;
import baliviya.com.github.MBO.utils.Const;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class RecipientDao extends AbstractDao<Recipient> {

    public void insert(Recipient r) {
        sql = "INSERT INTO " + Const.TABLE_NAME + ".RECIPIENTS " +
                "(CHAT_ID, FULL_NAME, IIN, PHONE_NUMBER, ADDRESS, VISA, APARTMENT, CHILDREN, PARENT_COUNT, SOCIAL_BENEFITS, STATUS, MARITAL_STATUS, ALIMENTS, EMPLOYMENT_TYPE, EMPLOYMENT, NEED_A_JOB, JOB_TYPE," +
                "EDUCATION, EDUCATION_NAME, DISABILITY_TYPE, DISABILITY,PROFESSIONAL_COURSES, EDUCATION_AND_OTHER_COURSES, BUSINESS_TRAINING, EDUCATION_COURSES_FOR_KIDS, ART_AND_MUSIC_COURSES," +
                "SPORT_SECTION, SOCIAL_NEEDS, PSYCHO_NEED, LAWYER_NEED, HEALER_FOR_FAMILY, CREDIT_HISTORY, CREDIT_INFO, REGISTRATION_DATE) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        getJdbcTemplate().update(sql, setParam(
                r.getChatId(), r.getFullName(), r.getIin(), r.getPhoneNumber(), r.getAddress(), r.getVisa(), r.getApartment(), r.getChildren(), r.getParentCount(), r.getSocialBenefits(), r.getStatus(),
                r.getMaritalStatus(), r.getAliments(), r.getEmploymentType(), r.getEmployment(), r.getNeedAJob(), r.getJobType(), r.getEducation(), r.getEducationName(), r.getDisabilityType(), r.getDisability(),
                r.getProfessionalCourses(), r.getEducationAndOtherCourses(), r.getBusinessTraining(), r.getEducationCoursesForKids(), r.getArtAndMusicCourses(), r.getSportSection(), r.getSocialNeeds(),
                r.getPsychoNeed(), r.getLawyerNeed(), r.getHealerForFamily(), r.getCreditHistory(), r.getCreditInfo(), r.getRegistrationDate()));
    }

    public boolean isRecipient(String iin) {
        sql = "SELECT count(*) FROM " + Const.TABLE_NAME + ".RECIPIENTS WHERE IIN = ?";
        int count = getJdbcTemplate().queryForObject(sql, setParam(iin), Integer.class);
        if (count > 0) {
            return true;
        } else {
            return false;
        }
    }

    public int getCount() {
        sql = "SELECT COUNT(ID) FROM " + Const.TABLE_NAME + ".RECIPIENTS";
        return getJdbcTemplate().queryForObject(sql, Integer.class);
    }

    public List<Recipient> getRecipientByTime(Date dateBegin, Date deadline) {
        sql = "SELECT * FROM " + Const.TABLE_NAME + ".RECIPIENTS WHERE REGISTRATION_DATE BETWEEN to_date(?,'YYYY-MM-DD') AND to_date(?, 'YYYY-MM-DD') ORDER BY ID";
        return getJdbcTemplate().query(sql, setParam(dateBegin, deadline), this::mapper);
    }

    @Override
    protected Recipient mapper(ResultSet rs, int index) throws SQLException {
        Recipient recipient = new Recipient();
        recipient.setId                             (rs.getInt(    1));
        recipient.setChatId                         (rs.getLong(   2));
        recipient.setFullName                       (rs.getString (3));
        recipient.setIin                            (rs.getString( 4));
        recipient.setPhoneNumber                    (rs.getString( 5));
        recipient.setAddress                        (rs.getString( 6));
        recipient.setVisa                           (rs.getString( 7));
        recipient.setApartment                      (rs.getString( 8));
        recipient.setChildren                       (rs.getString( 9));
        recipient.setParentCount                    (rs.getString(10));
        recipient.setSocialBenefits                 (rs.getString(11));
        recipient.setStatus                         (rs.getString(12));
        recipient.setMaritalStatus                  (rs.getString(13));
        recipient.setAliments                       (rs.getString(14));
        recipient.setEmploymentType                 (rs.getString(15));
        recipient.setEmployment                     (rs.getString(16));
        recipient.setNeedAJob                       (rs.getString(17));
        recipient.setJobType                        (rs.getString(18));
        recipient.setEducation                      (rs.getString(19));
        recipient.setEducationName                  (rs.getString(20));
        recipient.setDisabilityType                 (rs.getString(21));
        recipient.setDisability                     (rs.getString(22));
        recipient.setProfessionalCourses            (rs.getString(23));
        recipient.setEducationAndOtherCourses       (rs.getString(24));
        recipient.setBusinessTraining               (rs.getString(25));
        recipient.setEducationCoursesForKids        (rs.getString(26));
        recipient.setArtAndMusicCourses             (rs.getString(27));
        recipient.setSportSection                   (rs.getString(28));
        recipient.setSocialNeeds                    (rs.getString(29));
        recipient.setPsychoNeed                     (rs.getString(30));
        recipient.setLawyerNeed                     (rs.getString(31));
        recipient.setHealerForFamily                (rs.getString(32));
        recipient.setCreditHistory                  (rs.getString(33));
        recipient.setCreditInfo                     (rs.getString(34));
        recipient.setRegistrationDate               (rs.getDate(  35));
        return recipient;
    }
}
