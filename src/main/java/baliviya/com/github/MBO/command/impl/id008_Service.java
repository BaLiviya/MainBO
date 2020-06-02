//package baliviya.com.github.MBO.command.impl;
//
//import baliviya.com.github.MBO.command.Command;
//import baliviya.com.github.MBO.entity.custom.RegistrationService;
//import baliviya.com.github.MBO.entity.custom.Service;
//import baliviya.com.github.MBO.entity.custom.ServiceType;
//import baliviya.com.github.MBO.entity.enums.WaitingType;
//import baliviya.com.github.MBO.utils.ButtonsLeaf;
//import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
//import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
//
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//
//public class id008_Service extends Command {
//
//    private int                 deleteMessageId;
//    private int                 secondDeleteMessageId;
//    private long                userIin;
//    private ButtonsLeaf         buttonsLeaf;
//    private int                 serviceTypeId;
//    private List<String>        list = new ArrayList<>();
//    private List<ServiceType>   serviceTypes;
//    private List<Service>       services;
//    private Service             service;
//    private RegistrationService registrationService;
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
//                    registrationService = new RegistrationService();
//                    registrationService.setRegistrationDate(new Date());
//                    registrationService.setChatId(chatId);
//                    if (update.getMessage().getText().length() == 12) {
//                        try {
//                            userIin = Long.parseLong(updateMessageText);
//                        } catch (NumberFormatException e) {
//                            secondDeleteMessageId = wrongIinNotNumber();
//                            deleteMessageId = getIin();
//                            return COMEBACK;
//                        }
//                        if (!recipientDao.isRecipient(updateMessageText)) {
//                            deleteMessageId = registrationMessage();
//                            return EXIT;
//                        }
//                        registrationService.setIin(userIin);
//                        deleteMessageId = getServiceType();
//                        waitingType = WaitingType.SET_SERVICE_TYPE;
//                    } else {
//                        secondDeleteMessageId = wrongIin();
//                        deleteMessageId = getIin();
//                    }
//                } else {
//                    secondDeleteMessageId = wrongData();
//                    deleteMessageId = getIin();
//                }
//                return COMEBACK;
//            case SET_SERVICE_TYPE:
//                delete();
//                if (hasCallbackQuery()) {
//                    serviceTypeId = serviceTypes.get(Integer.parseInt(updateMessageText)).getId();
//                    registrationService.setServiceTypeId(serviceTypeId);
//                    deleteMessageId = getService();
//                    waitingType = WaitingType.SET_SERVICE;
//                } else {
//                    secondDeleteMessageId = wrongData();
//                    deleteMessageId = getServiceType();
//                }
//                return COMEBACK;
//            case SET_SERVICE:
//                delete();
//                if (hasCallbackQuery()) {
//                    service = services.get(Integer.parseInt(updateMessageText));
//                    registrationService.setServiceId(service.getId());
//                    String formatMessage = getText(1024);
//                    String result = String.format(formatMessage, service.getFullName(), service.getText());
//                    if (service.getPhoto() != null) {
//                        secondDeleteMessageId = bot.execute(new SendPhoto().setChatId(chatId).setPhoto(service.getPhoto())).getMessageId();
//                    }
//                    deleteMessageId = sendMessageWithKeyboard(result, 5);
//                    waitingType = WaitingType.SERVICE;
//                } else {
//                    secondDeleteMessageId = wrongData();
//                    deleteMessageId = getService();
//                }
//                return COMEBACK;
//            case SERVICE:
//                delete();
//                if (hasCallbackQuery()) {
//                    if (isButton(1004)) {
//                        registrationService.setCome(false);
//                        factory.getRegistrationServiceDao().insert(registrationService);
//                        deleteMessageId = done();
//                        return EXIT;
//                    } else if (isButton(1005)) {
//                        deleteMessageId = getServiceType();
//                        waitingType = WaitingType.SET_SERVICE_TYPE;
//                    }
//                } else {
//                    String formatMessage = getText(1024);
//                    String result = String.format(formatMessage, service.getFullName(), service.getText());
//                    if (service.getPhoto() != null) {
//                        secondDeleteMessageId = bot.execute(new SendPhoto().setChatId(chatId).setPhoto(service.getPhoto())).getMessageId();
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
//    private int getServiceType() throws TelegramApiException {
//        list.clear();
//        serviceTypes = factory.getServiceTypeDao().getAll();
//        serviceTypes.forEach((e) -> list.add(e.getName()));
//        buttonsLeaf = new ButtonsLeaf(list);
//        return toDeleteKeyboard(sendMessageWithKeyboard("Услуги", buttonsLeaf.getListButton()));
//    }
//
//    private int getService() throws TelegramApiException {
//        list.clear();
//        services = factory.getServiceDao().getAll(serviceTypeId);
//        services.forEach((e) -> list.add(e.getFullName()));
//        buttonsLeaf = new ButtonsLeaf(list);
//        return toDeleteKeyboard(sendMessageWithKeyboard("Выбор специалиста", buttonsLeaf.getListButton()));
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
