package baliviya.com.github.MBO.entity.standart;

import baliviya.com.github.MBO.entity.enums.Language;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LanguageUser {

    private long     chatId;
    private Language language;

}
