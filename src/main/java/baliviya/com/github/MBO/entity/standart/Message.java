package baliviya.com.github.MBO.entity.standart;

import baliviya.com.github.MBO.entity.enums.FileType;
import baliviya.com.github.MBO.entity.enums.Language;
import lombok.Data;

@Data
public class Message {

    private long     id;
    private String   name;
    private String   photo;
    private long     keyboardMarkUpId;
    private String   file;
    private FileType fileType;
    private Language language;

    public void setFile(String file, FileType fileType) {
        this.file = file;
        this.fileType = fileType;
    }

}
