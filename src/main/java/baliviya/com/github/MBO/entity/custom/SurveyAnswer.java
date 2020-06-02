package baliviya.com.github.MBO.entity.custom;

import lombok.Data;

@Data
public class SurveyAnswer {

    private int    id;
    private int    surveyId;
    private long   chatId;
    private String button;
    private String text;
}
