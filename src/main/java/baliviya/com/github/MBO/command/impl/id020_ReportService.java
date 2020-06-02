//package baliviya.com.github.MBO.command.impl;
//
//import baliviya.com.github.MBO.command.Command;
//import baliviya.com.github.MBO.entity.enums.WaitingType;
//import baliviya.com.github.MBO.services.ServiceReportService;
//import baliviya.com.github.MBO.utils.Const;
//import baliviya.com.github.MBO.utils.DateKeyboard;
//import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
//
//import java.util.Date;
//
//public class id020_ReportService extends Command {
//
//    private DateKeyboard dateKeyboard;
//    private Date start;
//    private Date end;
//
//    @Override
//    public boolean execute() throws TelegramApiException {
//        if (!isAdmin() && !isMainAdmin()) {
//            sendMessage(Const.NO_ACCESS);
//            return EXIT;
//        }
//        switch (waitingType) {
//            case START:
//                deleteMessage(updateMessageId);
//                dateKeyboard = new DateKeyboard();
//                sendStartDate();
//                waitingType = WaitingType.START_DATE;
//                return COMEBACK;
//            case START_DATE:
//                deleteMessage(updateMessageId);
//                if (hasCallbackQuery()) {
//                    if (dateKeyboard.isNext(updateMessageText)) {
//                        sendStartDate();
//                        return COMEBACK;
//                    }
//                    start = dateKeyboard.getDateDate(updateMessageText);
//                    start.setHours(0);
//                    start.setMinutes(0);
//                    start.setMinutes(0);
//                    sendEndDate();
//                    waitingType = WaitingType.END_DATE;
//                }
//                return COMEBACK;
//            case END_DATE:
//                deleteMessage(updateMessageId);
//                if (hasCallbackQuery()) {
//                    if (dateKeyboard.isNext(updateMessageText)) {
//                        sendStartDate();
//                        return COMEBACK;
//                    }
//                    end = dateKeyboard.getDateDate(updateMessageText);
//                    end.setHours(23);
//                    end.setMinutes(59);
//                    end.setSeconds(59);
//                    sendReport();
//                    waitingType = WaitingType.END_DATE;
//                }
//                return COMEBACK;
//        }
//        return EXIT;
//    }
//
//    private int sendStartDate() throws TelegramApiException {
//        return toDeleteKeyboard(sendMessageWithKeyboard(getText(1055), dateKeyboard.getCalendarKeyboard()));
//    }
//
//    private int sendEndDate() throws TelegramApiException {
//        return toDeleteKeyboard(sendMessageWithKeyboard(getText(1056), dateKeyboard.getCalendarKeyboard()));
//    }
//
//    private void sendReport() throws TelegramApiException {
//        int preview = sendMessage("Отчет подготавливается...");
//        ServiceReportService reportService = new ServiceReportService();
//        reportService.sendServiceReport(chatId, bot, start, end, preview);
//    }
//}
