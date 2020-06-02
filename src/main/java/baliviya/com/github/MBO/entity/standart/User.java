package baliviya.com.github.MBO.entity.standart;

import lombok.Data;

@Data
public class User {

    private int    id;
    private long   chatId;
    private String phone;
    private String fullName;
    private String email;
    private String userName;
    private String iin;
    private String status;
}
