package baliviya.com.github.MBO.command.impl;

import baliviya.com.github.MBO.command.Command;
import baliviya.com.github.MBO.services.DonateReportService;
import baliviya.com.github.MBO.utils.Const;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.SQLException;

public class id032_DonateReport extends Command {

    @Override
    public boolean execute() throws TelegramApiException, SQLException {
        if (!isAdmin()) {
            sendMessage(Const.NO_ACCESS);
            return EXIT;
        }
        switch (waitingType) {
            case START:
                deleteMessage(updateMessageId);
                sendReport();
        }
        return EXIT;
    }

    private void sendReport() throws TelegramApiException {
        int preview = sendMessage("Отчет подготавливается...");
        DonateReportService reportService = new DonateReportService();
        reportService.sendDonateReport(chatId, bot, preview);
    }
}
