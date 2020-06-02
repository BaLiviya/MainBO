package baliviya.com.github.MBO.entity.custom;

import lombok.Data;

import java.util.Date;

@Data
public class Task {
    private int    id;
    private int    statusId;
    private String taskText;
    private Date   dateBegin;
    private long   peopleId;
    private String peopleName;
    private int    district;
    private int    messageId;
    private long   employeeId;
    private int    groups;
}
