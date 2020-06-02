package baliviya.com.github.MBO.command.impl;

import baliviya.com.github.MBO.command.Command;
import baliviya.com.github.MBO.entity.custom.QuestMessage;
import baliviya.com.github.MBO.entity.custom.Question;
import baliviya.com.github.MBO.entity.custom.SurveyAnswer;
import baliviya.com.github.MBO.entity.enums.Language;
import baliviya.com.github.MBO.entity.enums.WaitingType;
import baliviya.com.github.MBO.services.LanguageService;
import baliviya.com.github.MBO.utils.ButtonsLeaf;
import baliviya.com.github.MBO.utils.Const;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class id010_Survey extends Command {

    private Language           currentLanguage;
    private List<Question>     allQuestion;
    private ButtonsLeaf        buttonsLeaf;
    private int                deleteMessageId;
    private Question           question;
    private List<QuestMessage> allMessage;
    private List<String>       listAnswers;
    private SurveyAnswer       surveyAnswer;

    @Override
    public boolean execute() throws TelegramApiException {
        switch (waitingType) {
            case START:
                deleteMessage(updateMessageId);
                deleteMessage(deleteMessageId);
                currentLanguage = LanguageService.getLanguage(chatId);
                allQuestion     = questionDao.getAllActive(currentLanguage, chatId);
                if (allQuestion == null || allQuestion.size() == 0) {
                    deleteMessageId = sendMessage(Const.EMPTY_SURVEY_MESSAGE);
                    return EXIT;
                }
                List<String> list   = new ArrayList<>();
                allQuestion.forEach((e) -> list.add(e.getName()));
                buttonsLeaf         = new ButtonsLeaf(list);
                deleteMessageId     = toDeleteKeyboard(sendMessageWithKeyboard(Const.SELECT_SURVEY_MESSAGE, buttonsLeaf.getListButton()));
                waitingType         = WaitingType.CHOOSE_QUESTION;
                return COMEBACK;
            case CHOOSE_QUESTION:
                deleteMessage(updateMessageId);
                deleteMessage(deleteMessageId);
                if (hasCallbackQuery()) {
                    question        = allQuestion.get(Integer.parseInt(updateMessageText));
                    allMessage      = questMessageDao.getAll(question.getId(), currentLanguage);
                    listAnswers     = new ArrayList<>();
                    allMessage.forEach((e) -> Collections.addAll(listAnswers, e.getRange().split(",")));
                    buttonsLeaf     = new ButtonsLeaf(listAnswers);
                    deleteMessageId = toDeleteKeyboard(sendMessageWithKeyboard(question.getDesc(), buttonsLeaf.getListButton()));
                    waitingType     = WaitingType.CHOOSE_OPTION;
                } else {
                    deleteMessageId = toDeleteKeyboard(sendMessageWithKeyboard(Const.SELECT_SURVEY_MESSAGE, buttonsLeaf.getListButton()));
                }
                return COMEBACK;
            case CHOOSE_OPTION:
                deleteMessage(updateMessageId);
                deleteMessage(deleteMessageId);
                if (hasCallbackQuery()) {
                    String answer = listAnswers.get(Integer.parseInt(updateMessageText));
                    for (QuestMessage questMessage : allMessage) {
                        for (String range: questMessage.getRange().split(",")) {
                            if (range.equals(answer)) {
                                deleteMessageId = sendMessage(questMessage.getMessage());
                                surveyAnswer    = new SurveyAnswer();
                                surveyAnswer.setButton(answer);
                                surveyAnswer.setChatId(chatId);
                                surveyAnswer.setSurveyId(question.getId());
                                surveyAnswer.setText("-");
                                factory.getSurveyAnswerDao().insert(surveyAnswer);
                                waitingType     = WaitingType.SET_TEXT;
                            }
                        }
                    }
                } else {
                    deleteMessageId = toDeleteKeyboard(sendMessageWithKeyboard(question.getDesc(), buttonsLeaf.getListButton()));
                }
                return COMEBACK;
            case SET_TEXT:
                deleteMessage(updateMessageId);
                deleteMessage(deleteMessageId);
                if (hasMessageText()) {
                    factory.getSurveyAnswerDao().update(surveyAnswer.getId(), updateMessageText);
                    allQuestion = questionDao.getAllActive(currentLanguage, chatId);
                    if (allQuestion == null || allQuestion.size() == 0) {
                        deleteMessageId = sendMessage(Const.EMPTY_SURVEY_MESSAGE);
                        return EXIT;
                    }
                    List<String> reloadList = new ArrayList<>();
                    allQuestion.forEach((e) -> reloadList.add(e.getName()));
                    buttonsLeaf             = new ButtonsLeaf(reloadList);
                    deleteMessageId         = toDeleteKeyboard(sendMessageWithKeyboard(Const.SELECT_SURVEY_MESSAGE, buttonsLeaf.getListButton()));
                    waitingType             = WaitingType.CHOOSE_QUESTION;
                }
                return COMEBACK;
        }
        return EXIT;
    }

}
