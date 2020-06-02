//package baliviya.com.github.MBO.command.impl;
//
//import baliviya.com.github.MBO.command.Command;
//import baliviya.com.github.MBO.entity.custom.Handling;
//import baliviya.com.github.MBO.entity.custom.RegistrationHandling;
//import baliviya.com.github.MBO.entity.custom.HandlingName;
//import baliviya.com.github.MBO.entity.enums.WaitingType;
//import baliviya.com.github.MBO.utils.ButtonsLeaf;
//import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
//import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
//
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//
//public class id007_SupportBusinessProject extends Command {
//
//    private int                  deleteMessageId;
//    private int                  secondDeleteMessageId;
//    private long                 iin;
//    private List<String>         list = new ArrayList<>();
//    private List<HandlingName>   handlingNames;
//    private ButtonsLeaf          buttonsLeaf;
//    private Handling             handling;
//    private RegistrationHandling registrationHandling;
//    private int                  businessId;
//
//    @Override
//    public boolean execute() throws TelegramApiException {
//        switch (waitingType) {
//            case START:
//                deleteMessage(updateMessageId);
//                deleteMessageId = getIin();
//                waitingType = WaitingType.SET_IIN;
//                return COMEBACK;
//            case SET_IIN:
//                delete();
//                if (hasMessageText()) {
//                    registrationHandling = new RegistrationHandling();
//                    registrationHandling.setRegistrationDate(new Date());
//                    registrationHandling.setChatId(chatId);
//                    if (update.getMessage().getText().length() == 12) {
//                        try {
//                            iin = Long.parseLong(updateMessageText);
//                        } catch (NumberFormatException e) {
//                            secondDeleteMessageId = wrongIinNotNumber();
//                            deleteMessageId = getIin();
//                            return COMEBACK;
//                        }
//                        if (!recipientDao.isRecipient(updateMessageText)) {
//                            deleteMessageId = registrationMessage();
//                            return EXIT;
//                        }
//                        registrationHandling.setIin(iin);
//                        deleteMessageId = getBusinessName();
//                        waitingType = WaitingType.SET_BUSINESS_NAME;
//                    } else {
//                        secondDeleteMessageId = wrongIin();
//                        deleteMessageId = getIin();
//                    }
//                } else {
//                    secondDeleteMessageId = wrongData();
//                    deleteMessageId = getIin();
//                }
//                return COMEBACK;
//            case SET_BUSINESS_NAME:
//                delete();
//                if (hasCallbackQuery()) {
//                    businessId = Integer.parseInt(updateMessageText);
//                    handling = factory.getHandlingDao().getBusiness(handlingNames.get(businessId).getId());
//                    String formatMessage = getText(1023);
//                    String result = String.format(formatMessage, handlingNames.get(businessId).getName(), handling.getText());
//                    if (handling.getPhoto() != null) {
//                        secondDeleteMessageId = bot.execute(new SendPhoto().setChatId(chatId).setPhoto(handling.getPhoto())).getMessageId();
//                    }
//                    deleteMessageId = sendMessageWithKeyboard(result, 5);
//                    waitingType = WaitingType.SET_BUSINESS;
//                } else {
//                    secondDeleteMessageId = wrongData();
//                    deleteMessageId = getBusinessName();
//                }
//                return COMEBACK;
//            case SET_BUSINESS:
//                delete();
//                if (hasCallbackQuery()) {
//                    if (isButton(1004)) {
//                        registrationHandling.setIdHandling(handling.getId());
//                        registrationHandling.setCome(false);
//                        factory.getRegistrationHandlingDao().insertBusiness(registrationHandling);
//                        deleteMessageId = done();
//                        return EXIT;
//                    } else if (isButton(1005)) {
//                        deleteMessageId = getBusinessName();
//                        waitingType = WaitingType.SET_BUSINESS_NAME;
//                    }
//                } else {
//                    String formatMessage = getText(1023);
//                    String result = String.format(formatMessage, handlingNames.get(businessId).getName(), handling.getText());
//                    if (handling.getPhoto() != null) {
//                        secondDeleteMessageId = bot.execute(new SendPhoto().setChatId(chatId).setPhoto(handling.getPhoto())).getMessageId();
//                    }
//                    deleteMessageId = sendMessageWithKeyboard(result, 5);
//                }
//                return COMEBACK;
//        }
//        return EXIT;
//    }
//
//    private int getIin() throws TelegramApiException {
//        return botUtils.sendMessage(1000, chatId);
//    }
//
//    private int wrongIin() throws TelegramApiException {
//        return botUtils.sendMessage(1018, chatId);
//    }
//
//    private int wrongIinNotNumber() throws TelegramApiException {
//        return botUtils.sendMessage(1020, chatId);
//    }
//
//    private int getBusinessName() throws TelegramApiException {
//        list.clear();
//        handlingNames = factory.getHandlingNameDao().getAllBusiness();
//        handlingNames.forEach((e) -> list.add(e.getName()));
//        buttonsLeaf = new ButtonsLeaf(list);
//        return toDeleteKeyboard(sendMessageWithKeyboard("<b>Сопровождение бизнес проектов</b>", buttonsLeaf.getListButton()));
//    }
//
//    private int registrationMessage() throws TelegramApiException {
//        return botUtils.sendMessage(1019, chatId);
//    }
//
//    private int wrongData() throws TelegramApiException {
//        return botUtils.sendMessage(1002, chatId);
//    }
//
//    private int done() throws TelegramApiException {
//        return botUtils.sendMessage(1017, chatId);
//    }
//
//    private void delete() {
//        deleteMessage(updateMessageId);
//        deleteMessage(deleteMessageId);
//        deleteMessage(secondDeleteMessageId);
//    }
//}
