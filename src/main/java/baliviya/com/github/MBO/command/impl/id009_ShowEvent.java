//package baliviya.com.github.MBO.command.impl;
//
//import baliviya.com.github.MBO.command.Command;
//import baliviya.com.github.MBO.entity.custom.Event;
//import baliviya.com.github.MBO.entity.enums.WaitingType;
//import baliviya.com.github.MBO.utils.ButtonsLeaf;
//import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
//import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class id009_ShowEvent extends Command {
//
//    private List<Event>  events;
//    private Event        event;
//    private ButtonsLeaf  buttonsLeaf;
//    private int          deleteMessageId;
//    private int          secondDeleteMessageId;
//
//    @Override
//    public boolean execute() throws TelegramApiException {
//        switch (waitingType) {
//            case START:
//                deleteMessage(updateMessageId);
//                events = factory.getEventDao().getAllActive();
//                if (events == null || events.size() == 0) {
//                    deleteMessageId = sendMessage(1025);
//                    return EXIT;
//                }
//                List<String> list = new ArrayList<>();
//                events.forEach((e) -> list.add(e.getName()));
//                buttonsLeaf = new ButtonsLeaf(list);
//                toDeleteKeyboard(sendMessageWithKeyboard("Активные мероприятия", buttonsLeaf.getListButton()));
//                waitingType = WaitingType.EVENT_SELECTION;
//                return COMEBACK;
//            case EVENT_SELECTION:
//                delete();
//                if (hasCallbackQuery()) {
//                    event = events.get(Integer.parseInt(updateMessageText));
//                    String formatMessage = getText(1026);
//                    String result = String.format(formatMessage, event.getName(), event.getText());
//                    if (event.getPhoto() != null) {
//                        secondDeleteMessageId = bot.execute(new SendPhoto().setChatId(chatId).setPhoto(event.getPhoto())).getMessageId();
//                    }
//                    deleteMessageId = sendMessageWithKeyboard(result, 6);
//                    waitingType = WaitingType.EVENT;
//                } else {
//                    secondDeleteMessageId = wrongData();
//                    toDeleteKeyboard(sendMessageWithKeyboard("Активные мероприятия", buttonsLeaf.getListButton()));
//                }
//                return COMEBACK;
//            case EVENT:
//                delete();
//                if (hasCallbackQuery()) {
//                    if (isButton(1005)) {
//                        events = factory.getEventDao().getAllActive();
//                        if (events == null || events.size() == 0) {
//                            deleteMessageId = sendMessage(1025);
//                            return EXIT;
//                        }
//                        List<String> backList = new ArrayList<>();
//                        events.forEach((e) -> backList.add(e.getName()));
//                        buttonsLeaf = new ButtonsLeaf(backList);
//                        toDeleteKeyboard(sendMessageWithKeyboard("Активные мероприятия", buttonsLeaf.getListButton()));
//                        waitingType = WaitingType.EVENT_SELECTION;
//                    }
//                } else {
//                    String formatMessage = getText(1026);
//                    String result = String.format(formatMessage, event.getName(), event.getText());
//                    if (event.getPhoto() != null) {
//                        secondDeleteMessageId = bot.execute(new SendPhoto().setChatId(chatId).setPhoto(event.getPhoto())).getMessageId();
//                    }
//                    deleteMessageId = sendMessageWithKeyboard(result, 6);
//                }
//                return COMEBACK;
//        }
//        return EXIT;
//    }
//
//    private void delete() {
//        deleteMessage(updateMessageId);
//        deleteMessage(deleteMessageId);
//        deleteMessage(secondDeleteMessageId);
//    }
//
//    private int wrongData() throws TelegramApiException {
//        return botUtils.sendMessage(1002, chatId);
//    }
//}
