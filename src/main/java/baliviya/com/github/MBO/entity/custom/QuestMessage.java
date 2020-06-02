package baliviya.com.github.MBO.entity.custom;

import lombok.Data;

@Data
public class QuestMessage {

    private int    id;
    private int    languageId;
    private String message;
    private String range;
    private int    idQuest;

    public QuestMessage setId(int id) {
        this.id = id;
        return this;
    }

    public QuestMessage setIdQuest(int idQuest) {
        this.idQuest = idQuest;
        return this;
    }

    public QuestMessage setRange(String range) {
        this.range = range;
        return this;
    }

    public QuestMessage setIdLanguage(int languageId) {
        this.languageId = languageId;
        return this;
    }
}
