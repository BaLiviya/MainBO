package baliviya.com.github.MBO.services;

import baliviya.com.github.MBO.command.Command;
import baliviya.com.github.MBO.command.CommandFactory;
import baliviya.com.github.MBO.entity.standart.Button;
import baliviya.com.github.MBO.exceptions.CommandNotFoundException;

public class CommandService {

    public Command getCommand(Button button) throws CommandNotFoundException {
        if (button.getCommandId() == 0) throw new CommandNotFoundException(new Exception("No data is available"));
        Command command = CommandFactory.getCommand(button.getCommandId());
        command.setId(button.getCommandId());
        command.setMessageId(button.getMessageId());
        return command;
    }
}
