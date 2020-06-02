package baliviya.com.github.MBO.entity.custom;

import lombok.Data;

import java.util.Date;

@Data
public class Suggestion {

    private int    id;
    private String fullName;
    private String phoneNumber;
    private String email;
    private String text;
    private Date   postDate;

}
