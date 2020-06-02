package baliviya.com.github.MBO.entity.custom;

import lombok.Data;

import java.util.Date;

@Data
public class Donate {

    private int     id;
    private Date    date;
    private String  charity;
    private String  peopleType;
    private String  donateType;
    private long    chatId;
    private String  district;
    private String  companyInfo;
}
