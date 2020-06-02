package baliviya.com.github.MBO.entity.custom;

import lombok.Data;

import java.util.Date;

@Data
public class TaskArchive {

    private int    id;
    private String text;
    private int    taskId;
    private Date   date;
}
