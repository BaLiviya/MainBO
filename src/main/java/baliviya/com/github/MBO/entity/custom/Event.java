package baliviya.com.github.MBO.entity.custom;

import lombok.Data;

@Data
public class Event {

    private int     id;
    private String  name;
    private String  photo;
    private String  text;
    private boolean isHide;
}
