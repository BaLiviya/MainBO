package baliviya.com.github.MBO.command.impl;

import baliviya.com.github.MBO.command.Command;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class id001_ShowInfo extends Command {

    @Override
    public boolean execute() throws TelegramApiException {
        deleteMessage(updateMessageId);
        sendMessageWithAddition();
        return EXIT;
    }
}
