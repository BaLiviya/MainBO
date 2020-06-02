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
//public class id006_Training extends Command {
//
//    private int                  deleteMessageId;
//    private int                  secondDeleteMessageId;
//    private long                 iin;
//    private List<String>         list = new ArrayList<>();
//    private List<HandlingName>   handlingNames;
//    private ButtonsLeaf          buttonsLeaf;
//    private Handling             handling;
//    private RegistrationHandling registrationHandling;
//    private int                  trainingId;
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
//                        deleteMessageId = getTrainingName();
//                        waitingType = WaitingType.SET_TRAINING_NAME;
//                    } else {
//                        secondDeleteMessageId = wrongIin();
//                        deleteMessageId = getIin();
//                    }
//                } else {
//                    secondDeleteMessageId = wrongData();
//                    deleteMessageId = getIin();
//                }
//                return COMEBACK;
//            case SET_TRAINING_NAME:
//                delete();
//                if (hasCallbackQuery()) {
//                    trainingId = Integer.parseInt(updateMessageText);
//                    handling = factory.getHandlingDao().getTraining(handlingNames.get(trainingId).getId());
//                    String formatMessage = getText(1022);
//                    String result = String.format(formatMessage, handlingNames.get(trainingId).getName(), handling.getText());
//                    if (handling.getPhoto() != null) {
//                        secondDeleteMessageId = bot.execute(new SendPhoto().setChatId(chatId).setPhoto(handling.getPhoto())).getMessageId();
//                    }
//                    deleteMessageId = sendMessageWithKeyboard(result, 5);
//                    waitingType = WaitingType.SET_TRAINING;
//                } else {
//                    secondDeleteMessageId = wrongData();
//                    deleteMessageId = getTrainingName();
//                }
//                return COMEBACK;
//            case SET_TRAINING:
//                delete();
//                if (hasCallbackQuery()) {
//                    if (isButton(1004)) {
//                        registrationHandling.setIdHandling(handling.getId());
//                        registrationHandling.setCome(false);
//                        factory.getRegistrationHandlingDao().insertTraining(registrationHandling);
//                        deleteMessageId = done();
//                        return EXIT;
//                    } else if (isButton(1005)) {
//                        deleteMessageId = getTrainingName();
//                        waitingType = WaitingType.SET_TRAINING_NAME;
//                    }
//                } else {
//                    String formatMessage = getText(1022);
//                    String result = String.format(formatMessage, handlingNames.get(trainingId).getName(), handling.getText());
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
//    private int getTrainingName() throws TelegramApiException {
//        list.clear();
//        handlingNames = factory.getHandlingNameDao().getAllTraining();
//        handlingNames.forEach((e) -> list.add(e.getName()));
//        buttonsLeaf = new ButtonsLeaf(list);
//        return toDeleteKeyboard(sendMessageWithKeyboard("<b>Тренинги</b>", buttonsLeaf.getListButton()));
//    }
//
//    private int wrongData() throws TelegramApiException {
//        return botUtils.sendMessage(1002, chatId);
//    }
//
//    private int registrationMessage() throws TelegramApiException {
//        return botUtils.sendMessage(1019, chatId);
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
