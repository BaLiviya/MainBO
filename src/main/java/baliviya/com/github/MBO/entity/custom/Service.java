package baliviya.com.github.MBO.entity.custom;

import lombok.Data;

@Data
public class Service {

    private int    id;
    private String fullName;
    private String photo;
    private String text;
    private int    serviceTypeId;
}
