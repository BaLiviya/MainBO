package baliviya.com.github.MBO.command.impl;

import baliviya.com.github.MBO.command.Command;
import baliviya.com.github.MBO.entity.custom.Donate;
import baliviya.com.github.MBO.entity.enums.WaitingType;
import baliviya.com.github.MBO.utils.ButtonsLeaf;
import baliviya.com.github.MBO.utils.Const;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class id024_CreateDonate extends Command {

    private List<String>    list;
    private ButtonsLeaf     buttonsLeaf;
    private int             deleteMessageId;
    private Donate          donate;
    private List<String >   answerList = new ArrayList<>();

    @Override
    public boolean  execute()           throws TelegramApiException, SQLException {
        switch (waitingType) {
            case START:
                deleteMessage(updateMessageId);
                deleteMessageId = getCharityType();
                waitingType     = WaitingType.SET_CHARITY_TYPE;
                return COMEBACK;
            case SET_CHARITY_TYPE:
                delete();
                if (hasCallbackQuery()) {
                    donate          = new Donate();
                    donate.setChatId(chatId);
                    donate.setCharity(list.get(Integer.parseInt(updateMessageText)));
                    deleteMessageId = getDistrict();
                    waitingType     = WaitingType.SET_DISTRICT;

                } else {
                    deleteMessageId = getCharityType();
                }
                return COMEBACK;
            case SET_DISTRICT:
                delete();
                if (hasCallbackQuery()) {
                    donate.setDistrict(list.get(Integer.parseInt(updateMessageText)));
                    deleteMessageId = getPeopleType();
                    waitingType     = WaitingType.SET_PEOPLE_TYPE;
                } else {
                    deleteMessageId = getDistrict();
                }
                return COMEBACK;
            case SET_PEOPLE_TYPE:
                delete();
                if (hasCallbackQuery()) {
                    if (list.get(Integer.parseInt(updateMessageText)).equals(getText(Const.NEXT_MESSAGE))) {
                        StringBuilder stringBuilder = new StringBuilder();
                        for (String answer : answerList) {
                            stringBuilder.append(answer).append(Const.SPLIT).append(space);
                        }
                        donate.setPeopleType(stringBuilder.toString().substring(0,stringBuilder.length() - 2));
                        deleteMessageId = getDonateType();
                        waitingType     = WaitingType.SET_DONATE_TYPE;
                        answerList.clear();
                    } else if (list.get(Integer.parseInt(updateMessageText)).equals(getText(Const.OTHERS_MESSAGE))) {
                        deleteMessageId = getOther();
                        waitingType     = WaitingType.SET_OTHER_PEOPLE_TYPE;
                    } else {
                        answerList.add(list.get(Integer.parseInt(updateMessageText)));
                        deleteMessageId = getPeopleType();
                    }
                } else {
                    deleteMessageId     = getPeopleType();
                }
                return COMEBACK;
            case SET_OTHER_PEOPLE_TYPE:
                delete();
                if (hasMessageText()) {
                    answerList.add(updateMessageText);
                    deleteMessageId = getPeopleType();
                    waitingType     = WaitingType.SET_PEOPLE_TYPE;
                }
                return COMEBACK;
            case SET_DONATE_TYPE:
                delete();
                if (hasCallbackQuery()) {
                    if (list.get(Integer.parseInt(updateMessageText)).equals(getText(Const.NEXT_MESSAGE))) {
                        StringBuilder stringBuilder = new StringBuilder();
                        for (String answer : answerList) {
                            stringBuilder.append(answer).append(Const.SPLIT).append(space);
                        }
                        donate.setDonateType(stringBuilder.toString().substring(0,stringBuilder.length() - 2));
                        deleteMessageId = getDonateMessage();
                        waitingType     = WaitingType.DONATE_TYPE_PEOPLE;
                    } else if (list.get(Integer.parseInt(updateMessageText)).equals(getText(Const.OTHERS_MESSAGE))) {
                        deleteMessageId = getOther();
                        waitingType     = WaitingType.SET_OTHER_DONATE_TYPE;
                    } else {
                        answerList.add(list.get(Integer.parseInt(updateMessageText)));
                        deleteMessageId = getDonateType();
                    }
                } else {
                    deleteMessageId = getDonateType();
                }
                return COMEBACK;
            case SET_OTHER_DONATE_TYPE:
                delete();
                if (hasMessageText()) {
                    answerList.add(updateMessageText);
                    deleteMessageId = getDonateType();
                    waitingType     = WaitingType.SET_DONATE_TYPE;
                }
                return COMEBACK;
            case DONATE_TYPE_PEOPLE:
                delete();
                if (hasCallbackQuery()) {
                    if (list.get(Integer.parseInt(updateMessageText)).equals("Физическое лицо")) {
                        deleteMessageId = doneMessage();
                        waitingType     = WaitingType.DONE_DONATE;
                    } else {
                        deleteMessageId = getPersonInfo();
                        waitingType     = WaitingType.COMPANY_INFO;
                    }
                } else {
                    deleteMessageId     = getDonateMessage();
                }
                return COMEBACK;
            case COMPANY_INFO:
                delete();
                if (hasMessageText()) {
                    donate.setCompanyInfo(updateMessageText);
                    donate.setDate(new Date());
                    factory.getDonateDao().insert(donate);
                    sendMessageWithKeyboard(getText(Const.DONE_DONATE_MESSAGE), Const.MAIN_MENU_KEYBOARD);
                    return EXIT;
                } else {
                    deleteMessageId = getPersonInfo();
                }
                return COMEBACK;
            case DONE_DONATE:
                delete();
                if (isButton(Const.SELECT_BUTTON)) {
                    donate.setDate(new Date());
                    factory.getDonateDao().insert(donate);
                    sendMessageWithKeyboard(getText(Const.DONE_DONATE_MESSAGE), Const.MAIN_MENU_KEYBOARD);
                    return EXIT;
                }
                return COMEBACK;
        }
        return EXIT;
    }

    private int     getCharityType()    throws TelegramApiException {
        list        = new ArrayList<>();
        Arrays.asList(getText(Const.OPEN_PRIVATE_MESSAGE).split(Const.SPLIT)).forEach(e -> list.add(e));
        buttonsLeaf = new ButtonsLeaf(list);
        return toDeleteKeyboard(sendMessageWithKeyboard(getText(Const.DONATE_TYPE_MESSAGE), buttonsLeaf.getListButton()));
    }

    private int     getDistrict()       throws TelegramApiException {
        list.clear();
        Arrays.asList(getText(Const.DISTRICT_TYPE_MESSAGE).split(Const.SPLIT)).forEach(e -> list.add(e));
        buttonsLeaf = new ButtonsLeaf(list);
        return toDeleteKeyboard(sendMessageWithKeyboard(getText(Const.SELECT_DISTRICT_MESSAGE), buttonsLeaf.getListButton()));
    }

    private int     getPeopleType()     throws TelegramApiException {
        list.clear();
        Arrays.asList(getText(Const.HELP_TYPE_MAN_MESSAGE).split(Const.SPLIT)).forEach(e -> list.add(e));
        list.add(getText(Const.NEXT_MESSAGE));
        buttonsLeaf = new ButtonsLeaf(list);
        return toDeleteKeyboard(sendMessageWithKeyboard(getText(Const.HELP_TYPE_PEOPLE_MESSAGE), buttonsLeaf.getListButton()));
    }

    private int     getOther()          throws TelegramApiException {
        return botUtils.sendMessage(Const.SET_YOUR_OPTION_MESSAGE, chatId);
    }

    private int     getDonateType()     throws TelegramApiException {
        list.clear();
        Arrays.asList(getText(Const.HELP_TYPE_MESSAGE).split(Const.SPLIT)).forEach(e -> list.add(e));
        list.add(getText(Const.NEXT_MESSAGE));
        buttonsLeaf = new ButtonsLeaf(list);
        return toDeleteKeyboard(sendMessageWithKeyboard(getText(Const.SELECT_HELP_TYPE_MESSAGE), buttonsLeaf.getListButton()));
    }

    private int     doneMessage()       throws TelegramApiException {
        return toDeleteKeyboard(sendMessageWithKeyboard(getMessageDone(),Const.DONATE_KEYBOARD));
    }

    private int     getPersonInfo()     throws TelegramApiException {
        return botUtils.sendMessage(Const.COMPANY_INFO_CONTACT_MESSAGE, chatId);
    }

    private int     getDonateMessage()  throws TelegramApiException {
        list.clear();
        Arrays.asList("Физическое лицо;Юридическое лицо".split(Const.SPLIT)).forEach(e -> list.add(e));
        buttonsLeaf = new ButtonsLeaf(list);
        return toDeleteKeyboard(sendMessageWithKeyboard(getText(Const.SELECT_YOUR_STATUS_MESSAGE), buttonsLeaf.getListButton()));
    }

    private void    delete() {
        deleteMessage(updateMessageId);
        deleteMessage(deleteMessageId);
    }

    private String  getMessageDone() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(next);
        stringBuilder.append(getItalicsText("Вы решили оказать благотворительную помощь : ")).append(getBoldText(donate.getCharity())).append(next);
        stringBuilder.append(getItalicsText("Вы выбрали : ")).append(getBoldText(donate.getPeopleType())).append(getItalicsText(" для оказания помощи")).append(next);
        stringBuilder.append(getItalicsText("Вы выбрали тип благотворительной помощи : ")).append(getBoldText(donate.getDonateType())).append(next);
        stringBuilder.append(getItalicsText("Если данные верны нажмите на кнопку ") + getBoldText("Отправить"));
        return stringBuilder.toString();
    }
}
