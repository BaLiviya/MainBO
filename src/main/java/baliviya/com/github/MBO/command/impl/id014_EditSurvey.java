package baliviya.com.github.MBO.command.impl;

import baliviya.com.github.MBO.command.Command;
import baliviya.com.github.MBO.entity.custom.QuestMessage;
import baliviya.com.github.MBO.entity.custom.Question;
import baliviya.com.github.MBO.entity.enums.Language;
import baliviya.com.github.MBO.entity.enums.WaitingType;
import baliviya.com.github.MBO.services.LanguageService;
import baliviya.com.github.MBO.utils.ButtonUtil;
import baliviya.com.github.MBO.utils.ButtonsLeaf;
import baliviya.com.github.MBO.utils.Const;
import baliviya.com.github.MBO.utils.UpdateUtil;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class id014_EditSurvey extends Command {

    private ButtonsLeaf         buttonsLeaf;
    private List<Question>      all;
    private int                 questId;
    private int                 questMessageId;
    private Question            question;
    private QuestMessage        questMessage;
    private List<QuestMessage>  questMessageList;
    private Language            currentLanguage;
    private int                 editionMessageId;
    private QuestMessage        addQuestMessage;
    private WaitingType         updateType = WaitingType.START;


    @Override
    public boolean  execute()           throws TelegramApiException {
        if (!isAdmin()) {
            sendMessage(Const.NO_ACCESS);
            return EXIT;
        }
        switch (updateType) {
            case UPDATE_QUEST:
                if (isCommand()) return COMEBACK;
                break;
            case UPDATE_MESSAGE:
                if (isCommandMessage()) return COMEBACK;
                break;
        }
        switch (waitingType) {
            case START:
                deleteMessage(updateMessageId);
                currentLanguage = LanguageService.getLanguage(chatId);
                all             = questionDao.getAll(currentLanguage);
                buttonsLeaf     = new ButtonsLeaf(all.stream().map(Question::getName).collect(Collectors.toList()));
                toDeleteKeyboard(sendMessageWithKeyboard("Выберите опрос для редактирования", buttonsLeaf.getListButton())); // TODO: 15.03.2020 сделать запись в бд
                waitingType     = WaitingType.CHOOSE_QUESTION;
                return COMEBACK;
            case CHOOSE_QUESTION:
                deleteMessage(updateMessageId);
                if (hasCallbackQuery()) {
                    if (buttonsLeaf.isNext(updateMessageText)) toDeleteKeyboard(sendMessageWithKeyboard("Выберите опрос для редактирования", buttonsLeaf.getListButton()));
                    questId     = all.get(Integer.parseInt(updateMessageText)).getId();
                    sendMessage(1045);
                    waitingType = WaitingType.EDITION;
                    updateType  = WaitingType.UPDATE_QUEST;
                    sendEditor();
                } else {
                    toDeleteKeyboard(sendMessageWithKeyboard("Выберите опрос для редактирования", buttonsLeaf.getListButton()));
                }
                return COMEBACK;
            case EDITION_MESSAGE:
                isCommandMessage();
                return COMEBACK;
            case SET_NAME:
                if (hasMessageText()) {
                    question.setName(ButtonUtil.getButtonName(updateMessageText,200));
                    questionDao.update(question);
                    sendEditor();
                }
                return COMEBACK;
            case SET_DESCRIPTION:
                if (hasMessageText()) {
                    question.setDesc(updateMessageText);
                    questionDao.update(question);
                    sendEditor();
                }
                return COMEBACK;
            case SET_RANGE:
                if (hasMessageText()) {
                    if (questMessage.getRange().split(Const.SPLIT_RANGE).length == updateMessageText.split(Const.SPLIT_RANGE).length) {
                        questMessage.setRange(updateMessageText);
                        questMessageDao.update(questMessage);
                    } else {
                        for (Language language : Language.values()) {
                            QuestMessage byId = questMessageDao.getById(questMessageId, language);
                            byId.setRange(updateMessageText);
                            questMessageDao.update(byId);
                        }
                        sendMessage("Количество вариантов не совпадает с ранней версией - данные обновлены для всех языков");
                    }
                    sendEditorMessage();
                }
                return COMEBACK;
            case SET_MESSAGE:
                if (hasMessageText()) {
                    questMessage.setMessage(updateMessageText);
                    questMessageDao.update(questMessage);
                    sendEditorMessage();
                }
                return COMEBACK;
            case DELETE:
                if (hasMessageText() && updateMessageText.equals("удалить")) {
                    factory.getSurveyAnswerDao().deleteByQuestId(questId);
                    questMessageDao.deleteByQuestId(questId);
                    questionDao.delete(questId);
                    log.info("Deleted question №{} - {}", questId, UpdateUtil.getUser(update).toString());
                    sendMessage(1050);
                    return EXIT;
                }
                return COMEBACK;
            case DELETE_MESSAGE:
                if (hasMessageText() && updateMessageText.equalsIgnoreCase("удалить")) {
                    questMessageDao.delete(questMessageId);
                    log.info("Deleted message №{} for quest №{} - {}", questMessageId, questId, UpdateUtil.getUser(update).toString());
                    sendMessage(1049);
                    sendEditor();
                }
                return COMEBACK;
            case GET_RANGE:
                if (hasMessageText()) {
                    addQuestMessage = new QuestMessage();
                    addQuestMessage.setRange(updateMessageText).setIdQuest(questId);
                    sendMessage("Напишите сообщение для этой группы. Пример:\nНам очень жаль что мы не оправдали Ваши ожидания. Хотелось бы узнать что нам необходимо улучшить?");
                    waitingType     = WaitingType.GET_MESSAGE;
                }
                return COMEBACK;
            case GET_MESSAGE:
                if (hasMessageText()) {
                    addQuestMessage.setMessage(updateMessageText);
                    addQuestMessage.setId(questMessageDao.getNextId("QUEST_MESSAGE"));
                    for (Language language : Language.values()) {
                        questMessageDao.insert(addQuestMessage.setIdLanguage(language.getId()));
                    }
                    sendEditor();
                }
                return COMEBACK;
        }
        return EXIT;
    }

    private void    sendEditor()        throws TelegramApiException {
        deleteMessage(editionMessageId);
        deleteMessage(updateMessageId);
        loadQuest();
        String text         = String.format(getText(1046), messageDao.getMessageText(1047, currentLanguage), question.getName(), question.getDesc());
        buttonsLeaf         = new ButtonsLeaf(questMessageList.stream().map(QuestMessage::getRange).collect(Collectors.toList()));
        editionMessageId    = sendMessageWithKeyboard(text, buttonsLeaf.getListButton());
        toDeleteKeyboard(editionMessageId);
    }

    private void    loadQuest() {
        question            = questionDao.getById(questId, currentLanguage);
        questMessageList    = questMessageDao.getAll(questId, currentLanguage);
    }

    private boolean isCommand()         throws TelegramApiException {
        if (hasCallbackQuery()) {
            questMessageId  = questMessageList.get(Integer.parseInt(updateMessageText)).getId();
            updateType      = WaitingType.UPDATE_MESSAGE;
            sendEditorMessage();
        } else if (isButton(1009)) {
            sendMessage("Введите новое название");
            waitingType = WaitingType.SET_NAME;
        } else if (isButton(47)) {
            sendMessage("Введите новое описание");
            waitingType = WaitingType.SET_DESCRIPTION;
        } else if (isButton(1011)) {
            sendMessage("Напишите слово 'удалить' для подтвержения. Внимание, вместе с опросом удаляется ответы на этот опрос.");
            waitingType = WaitingType.DELETE;
        } else if (isButton(50)) {
            changeLanguage();
            sendEditor();
        } else if (isButton(1010)) {
            sendMessage("Добавьте группу ответов на русском через '%s' Примеры \n1,2,3,4\nхорошо,средне,плохо\n*,**,*** ");
            waitingType = WaitingType.GET_RANGE;
        } else {
            return COMEBACK;
        }
        return EXIT;
    }

    private void    sendEditorMessage() throws TelegramApiException {
        deleteMessage(editionMessageId);
        deleteMessage(updateMessageId);
        questMessage    = questMessageDao.getById(questMessageId, currentLanguage);
        String text     = String.format(getText(1048), messageDao.getMessageText(1047, currentLanguage), questMessage.getRange(), questMessage.getMessage());
        sendMessageWithKeyboard(text, 21);
        waitingType     = WaitingType.EDITION_MESSAGE;
    }

    private void    changeLanguage() {
        if (currentLanguage == Language.ru) {
            currentLanguage = Language.kz;
        } else {
            currentLanguage = Language.ru;
        }
    }

    public boolean  isCommandMessage()  throws TelegramApiException {
        if (isButton(1012)) {
            sendMessage("Введите новые варианты ответа");
            waitingType = WaitingType.SET_RANGE;
        } else if (isButton(47)) {
            sendMessage("Введите новое сообщение");
            waitingType = WaitingType.SET_MESSAGE;
        } else if (isButton(50)) {
            changeLanguage();
            sendEditorMessage();
        } else if (isButton(1011)) {
            sendMessage("Напишите слово 'удалить' для подтверждения");
            waitingType = WaitingType.DELETE_MESSAGE;
        } else if (isButton(1005)) {
            updateType = WaitingType.UPDATE_QUEST;
            sendMessage(1045);
            sendEditor();
        } else {
            return COMEBACK;
        }
        return EXIT;
    }
}
