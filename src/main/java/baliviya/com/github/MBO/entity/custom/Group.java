package baliviya.com.github.MBO.entity.custom;

import lombok.Data;

@Data
public class Group {

    private int     id;
    private String  names;
    private long    chatId;
    private String  userName;
    private boolean isRegistered;
    private String  message;
    private boolean isCanWithoutTag;
    private boolean isCanPhoto;
    private boolean isCanVideo;
    private boolean isCanAudio;
    private boolean isCanFile;
    private boolean isCanLink;
    private boolean isCanSticker;
}
