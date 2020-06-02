package baliviya.com.github.MBO.entity.custom;

import lombok.Data;

import java.util.Date;

@Data
public class RegistrationHandling {

    private int     id;
    private long    chatId;
    private long    iin;
    private int     idHandling;
    private Date    registrationDate;
    private boolean isCome;
}
