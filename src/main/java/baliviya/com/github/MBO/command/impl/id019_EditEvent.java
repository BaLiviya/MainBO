//package baliviya.com.github.MBO.command.impl;
//
//import baliviya.com.github.MBO.command.Command;
//import baliviya.com.github.MBO.entity.custom.Event;
//import baliviya.com.github.MBO.entity.enums.WaitingType;
//import baliviya.com.github.MBO.utils.Const;
//import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
//
//import java.util.List;
//
//public class id019_EditEvent extends Command {
//
//    private List<Event> events;
//    private Event event;
//    private int deleteMessageId;
//    private int eventId;
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
//                sendEvent();
//                waitingType = WaitingType.CHOOSE_EVENT;
//                return COMEBACK;
//            case CHOOSE_EVENT:
//                deleteMessage(deleteMessageId);
//                deleteMessage(updateMessageId);
//                if (hasMessageText()) {
//                    if (isCommand("/del")) {
//                        eventId = events.get(getInt()).getId();
//                        eventDao.delete(eventId);
//                        sendEvent();
//                        waitingType = WaitingType.START;
//                    } else if (isCommand("/new")) {
//                        deleteMessageId = sendMessage("Введите название для нового мероприятия");
//                        waitingType = WaitingType.NEW_EVENT;
//                    } else if (isCommand("/st")) {
//                        event = events.get(getInt());
//                        event.setHide(!event.isHide());
//                        eventDao.updateStatus(event);
//                        sendEvent();
//                        waitingType = WaitingType.START;
//                    }
//                }
//                return COMEBACK;
//            case NEW_EVENT:
//                deleteMessage(updateMessageId);
//                deleteMessage(deleteMessageId);
//                if (hasMessageText()) {
//                    event = new Event();
//                    event.setName(updateMessageText);
//                    deleteMessageId = sendMessage("Отправьте фото или картинку мероприятия");
//                    waitingType = WaitingType.SET_PHOTO;
//                }
//                return COMEBACK;
//            case SET_PHOTO:
//                deleteMessage(updateMessageId);
//                deleteMessage(deleteMessageId);
//                if (hasPhoto()) {
//                    event.setPhoto(updateMessagePhoto);
//                    deleteMessageId = sendMessage("Напишите информацию о мероприятии");
//                    waitingType = WaitingType.SET_TEXT_EVENT;
//                }
//                return COMEBACK;
//            case SET_TEXT_EVENT:
//                deleteMessage(updateMessageId);
//                deleteMessage(deleteMessageId);
//                if (hasMessageText()) {
//                    event.setText(updateMessageText);
//                    event.setHide(false);
//                    eventDao.insert(event);
//                    sendEvent();
//                    waitingType = WaitingType.START;
//                }
//                return COMEBACK;
//        }
//        return EXIT;
//    }
//
//    private void sendEvent() throws TelegramApiException {
//        String formatMessage = getText(1058);
//        StringBuilder infoByEvent = new StringBuilder();
//        events = eventDao.getAll();
//        String format = getText(1059);
//        for (int i = 0; i < events.size(); i++) {
//            event = events.get(i);
//            infoByEvent.append(String.format(format,"/del" + i, "/st" + i, event.isHide() ? "❌" : "✅" , event.getName())).append(next);
//        }
//        deleteMessageId = sendMessage(String.format(formatMessage, infoByEvent.toString(), "/new"));
//    }
//
//    private boolean isCommand(String command) {
//        return updateMessageText.startsWith(command);
//    }
//
//    private int getInt() {
//        return Integer.parseInt(updateMessageText.replaceAll("[^0-9]", ""));
//    }
//}
