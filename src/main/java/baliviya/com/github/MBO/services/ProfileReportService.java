package baliviya.com.github.MBO.services;

import baliviya.com.github.MBO.dao.DaoFactory;
import baliviya.com.github.MBO.dao.impl.MessageDao;
import baliviya.com.github.MBO.dao.impl.RecipientDao;
import baliviya.com.github.MBO.entity.custom.Recipient;
import baliviya.com.github.MBO.entity.enums.Language;
import baliviya.com.github.MBO.utils.Const;
import baliviya.com.github.MBO.utils.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class ProfileReportService {

    private DaoFactory    daoFactory      = DaoFactory.getInstance();
    private MessageDao    messageDao      = daoFactory.getMessageDao();
    private RecipientDao  recipientDao    = daoFactory.getRecipientDao();
    private XSSFWorkbook  workbook        = new XSSFWorkbook();
    private XSSFCellStyle style           = workbook.createCellStyle();
    private Language      currentLanguage = Language.ru;
    private Sheet         sheets;
    private Sheet         sheet;
    private int           count;

    public void sendProfileReport(long chatId, DefaultAbsSender bot, Date dateBegin, Date dateEnd, int messagePrevReport) {
        currentLanguage = LanguageService.getLanguage(chatId);
        try {
            sendReport(chatId, bot, dateBegin, dateEnd, messagePrevReport);
        } catch (Exception e) {
            log.error("Can't create/send report", e);
            try {
                bot.execute(new SendMessage(chatId, "Ошибка при создании отчета"));
            } catch (TelegramApiException ex) {
                log.error("Can't send message", ex);
            }
        }
    }

    private void sendReport(long chatId, DefaultAbsSender bot, Date dateBegin, Date dateEnd, int messagePrevReport) throws TelegramApiException, IOException {
        sheets = workbook.createSheet("Анкета");
        sheet = sheets;
        List<Recipient> recipients = recipientDao.getRecipientByTime(dateBegin, dateEnd);
        if (recipients == null || recipients.size() == 0) {
            bot.execute(new DeleteMessage(chatId, messagePrevReport));
            bot.execute(new SendMessage(chatId, "За выбранный период заявки отсутствует"));
            return;
        }
        BorderStyle thin = BorderStyle.THIN;
        short black = IndexedColors.BLACK.getIndex();
        XSSFCellStyle styleTitle = setStyle(workbook, thin, black, style);
        int rowIndex = 0;
        createTitle(styleTitle, rowIndex, Arrays.asList(messageDao.getMessageText(1057).split(Const.SPLIT)));
        List<List<String>> info = recipients.stream().map(x -> {
            List<String> list = new ArrayList<>();
            list.add(String.valueOf(x.getId()));
            list.add(x.getFullName());
            list.add(x.getIin());
            list.add(x.getPhoneNumber());
            list.add(x.getAddress());
            list.add(x.getVisa());
            list.add(x.getApartment());
            list.add(x.getChildren());
            list.add(x.getParentCount());
            list.add(x.getSocialBenefits());
            list.add(x.getStatus());
            list.add(x.getMaritalStatus());
            list.add(x.getAliments());
            list.add(x.getEmploymentType().equals("Работаю") ? x.getEmploymentType() + " - " + x.getEmployment() : x.getEmploymentType());
            list.add(x.getNeedAJob().equals("Нуждаюсь") ? x.getNeedAJob() + " - " + x.getJobType() : x.getNeedAJob());
            list.add(x.getEducation().equals("Школа") ? x.getEducation() : x.getEducation() + " - " + x.getEducationName());
            list.add(x.getDisabilityType() == null ? " " : x.getDisabilityType() + " - " + x.getDisability() == null ? " " : x.getDisability());
            list.add(x.getProfessionalCourses());
            list.add(x.getEducationAndOtherCourses());
            list.add(x.getBusinessTraining());
            list.add(x.getEducationCoursesForKids());
            list.add(x.getArtAndMusicCourses());
            list.add(x.getSportSection());
            list.add(x.getSocialNeeds());
            list.add(x.getPsychoNeed());
            list.add(x.getLawyerNeed());
            list.add(x.getHealerForFamily());
            list.add(x.getCreditHistory() + " - " + x.getCreditInfo() == null ? " " : x.getCreditInfo());
            count = list.size();
            return list;
        }).collect(Collectors.toList());
        addInfo(info, rowIndex);
        sendFile(chatId, bot, dateBegin, dateEnd);

    }

    private void addInfo(List<List<String>> reports, int rowIndex) {
        int cellIndex;
        for (List<String> report: reports) {
            sheets.createRow(++rowIndex);
            insertToRow(rowIndex, report, style);
        }
        for (cellIndex = 0; cellIndex <= count; cellIndex++) {
            sheets.autoSizeColumn(cellIndex);
        }
    }

    private void createTitle(XSSFCellStyle styleTitle, int rowIndex, List<String> title) {
        sheets.createRow(rowIndex);
        insertToRow(rowIndex, title, styleTitle);
    }

    private void insertToRow(int row, List<String> cellValues, CellStyle cellStyle) {
        int cellIndex = 0;
        for (String cellValue : cellValues) {
            addCellValue(row, cellIndex++, cellValue, cellStyle);
        }
    }

    private void addCellValue(int rowIndex, int cellIndex, String cellValue, CellStyle cellStyle) {
        sheets.getRow(rowIndex).createCell(cellIndex).setCellValue(getString(cellValue));
        sheet.getRow(rowIndex).getCell(cellIndex).setCellStyle(cellStyle);
    }

    private String getString(String nullable) {
        if (nullable == null) return "";
        return nullable;
    }

    private XSSFCellStyle setStyle(XSSFWorkbook workbook, BorderStyle thin, short black, XSSFCellStyle style) {
        style.setWrapText(true);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFillBackgroundColor(IndexedColors.BLUE.getIndex());
        style.setBorderTop(thin);
        style.setBorderBottom(thin);
        style.setBorderRight(thin);
        style.setBorderLeft(thin);
        style.setTopBorderColor(black);
        style.setRightBorderColor(black);
        style.setBottomBorderColor(black);
        style.setLeftBorderColor(black);
        BorderStyle tittle = BorderStyle.MEDIUM;

        XSSFCellStyle styleTitle = workbook.createCellStyle();
        styleTitle.setWrapText(true);
        styleTitle.setAlignment(HorizontalAlignment.CENTER);
        styleTitle.setVerticalAlignment(VerticalAlignment.CENTER);
        styleTitle.setBorderTop(tittle);
        styleTitle.setBorderBottom(tittle);
        styleTitle.setBorderRight(tittle);
        styleTitle.setBorderLeft(tittle);
        styleTitle.setTopBorderColor(black);
        styleTitle.setRightBorderColor(black);
        styleTitle.setBottomBorderColor(black);
        styleTitle.setLeftBorderColor(black);
        style.setFillForegroundColor(new XSSFColor(new Color(0, 52, 94)));
        return styleTitle;
    }

    private void sendFile(long chatId, DefaultAbsSender bot, Date dateBegin, Date dateEnd) throws IOException, TelegramApiException {
        String fileName = "Анкета за: " + DateUtil.getDayDate(dateBegin) + " - " + DateUtil.getDayDate(dateEnd) + ".xlsx";
        String path = "C:\\" + fileName;
        path += new Date().getTime();
        try (FileOutputStream stream = new FileOutputStream(path)) {
            workbook.write(stream);
        } catch (IOException e) {
            log.error("Can't send file error: ", e);
        }
        sendFile(chatId, bot, fileName, path);
    }

    private void sendFile(long chatId, DefaultAbsSender bot, String fileName, String path) throws IOException, TelegramApiException {
        File file = new File(path);
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            bot.execute(new SendDocument().setChatId(chatId).setDocument(fileName, fileInputStream));
        }
        file.delete();
    }
}
