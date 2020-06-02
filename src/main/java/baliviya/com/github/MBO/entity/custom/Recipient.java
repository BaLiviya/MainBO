package baliviya.com.github.MBO.entity.custom;

import lombok.Data;

import java.util.Date;

@Data
public class Recipient {

    private int     id;
    private long    chatId;
    private String  fullName;
    private String  iin;
    private String  phoneNumber;
    private String  address;
    private String  visa;
    private String  apartment;
    private String  children;
    private String  parentCount;
    private String  socialBenefits;
    private String  status;
    private String  maritalStatus;
    private String  aliments;
    private String  employmentType;
    private String  employment;
    private String  needAJob;
    private String  jobType;
    private String  education;
    private String  educationName;
    private String  disabilityType;
    private String  disability;
    private String  professionalCourses;
    private String  educationAndOtherCourses;
    private String  businessTraining;
    private String  educationCoursesForKids;
    private String  artAndMusicCourses;
    private String  sportSection;
    private String  socialNeeds;
    private String  psychoNeed;
    private String  lawyerNeed;
    private String  healerForFamily;
    private String  creditHistory;
    private String  creditInfo;
    private Date    registrationDate;
}
