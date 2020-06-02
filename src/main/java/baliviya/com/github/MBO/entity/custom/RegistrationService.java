package baliviya.com.github.MBO.entity.custom;

import lombok.Data;

import java.util.Date;

@Data
public class RegistrationService {

    private int     id;
    private long    chatId;
    private long    iin;
    private int     serviceTypeId;
    private int     serviceId;
    private Date    registrationDate;
    private boolean isCome;
}
