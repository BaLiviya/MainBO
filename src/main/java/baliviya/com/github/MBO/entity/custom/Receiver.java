package baliviya.com.github.MBO.entity.custom;

import lombok.Data;

import java.util.Date;

@Data
public class Receiver {
    private int    id;
    private Date   date;
    private String charityType;
    private String text;
    private long   chatId;
}
