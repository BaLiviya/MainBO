package baliviya.com.github.MBO.services;

import baliviya.com.github.MBO.dao.DaoFactory;
import baliviya.com.github.MBO.dao.impl.DonateDao;
import baliviya.com.github.MBO.dao.impl.ReceiverDao;
import baliviya.com.github.MBO.dao.impl.UserDao;
import baliviya.com.github.MBO.entity.custom.Donate;
import baliviya.com.github.MBO.entity.custom.Receiver;
import baliviya.com.github.MBO.entity.standart.User;
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

import java.awt.*;
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
public class DonateReportService {

    private DaoFactory      factory     = DaoFactory.getInstance();
    private DonateDao       donateDao   = factory.getDonateDao();
    private UserDao         userDao     = factory.getUserDao();
    private ReceiverDao     receiverDao = factory.getReceiverDao();
    private XSSFWorkbook    workbook    = new XSSFWorkbook();
    private XSSFCellStyle   style       = workbook.createCellStyle();
    private Sheet           firstSheet;
    private Sheet           secondSheet;

    public void             sendDonateReport(long chatId, DefaultAbsSender bot, int messagePrevReport) throws TelegramApiException {
        try {
            sendReport(chatId, bot, messagePrevReport);
        } catch (Exception e) {
            log.error("Can't create/send report", e);
            try {
                bot.execute(new SendMessage(chatId, "Ошибка при создании отчета"));
            } catch (TelegramApiException ex) {
                log.error("Can't send message", ex);
            }
        }
    }

    private void            sendReport(long chatId, DefaultAbsSender bot, int messagePrevReport) throws IOException, TelegramApiException {
        firstSheet                  = workbook.createSheet("Оказание");
        secondSheet                 = workbook.createSheet("Получение");

        List<Donate> all            = donateDao.getAll();
        List<Receiver> receiverAll  = receiverDao.getAll();

        if (all == null || all.size() == 0 && receiverAll == null || receiverAll.size() == 0) {
            bot.execute(new DeleteMessage(chatId, messagePrevReport));
            bot.execute(new SendMessage(chatId, "заявки отсутствуют"));
            return;
        }
        BorderStyle thin         = BorderStyle.THIN;
        short black              = IndexedColors.BLACK.getIndex();
        XSSFCellStyle styleTitle = setStyle(workbook, thin, black, style);
        int rowIndex             = 1;

        createTitle(styleTitle, rowIndex, Arrays.asList("№;ФИО;Номер телефона;Тип помощи;Район;Категория лиц; Метод помощи;Юридические данные;Дата".split(Const.SPLIT)));
        List<List<String>> info = all.stream().map(x -> {
            List<String> list   = new ArrayList<>();
            User user           = userDao.getUserByChatId(x.getChatId());
            list.add(String.valueOf(x.getId()));
            list.add(user.getFullName());
            list.add(user.getPhone());
            list.add(x.getCharity());
            list.add(x.getDistrict());
            list.add(x.getPeopleType());
            list.add(x.getDonateType());
            list.add(x.getCompanyInfo() != null? x.getCompanyInfo() : " ");
            list.add(String.valueOf(x.getDate()));
            return list;
        }).collect(Collectors.toList());

        createSecondTitle(styleTitle, rowIndex, Arrays.asList("№;ФИО;Номер телефона;Тип помощи;Текст;Дата".split(Const.SPLIT)));
        List<List<String>> secondInfo   = receiverAll.stream().map(x -> {
            List<String> list           = new ArrayList<>();
            User user                   = userDao.getUserByChatId(x.getChatId());
            list.add(String.valueOf(x.getId()));
            list.add(user.getFullName());
            list.add(user.getPhone());
            list.add(x.getCharityType());
            list.add(x.getText());
            list.add(String.valueOf(x.getDate()));
            return list;
        }).collect(Collectors.toList());

        addSecondInfo(secondInfo, rowIndex);
        addInfo(info, rowIndex);
        sendFile(chatId, bot);
    }

    private void            sendHelp(long chatId, DefaultAbsSender bot, int messagePrevReport) throws TelegramApiException {
        secondSheet = workbook.createSheet("Получение");
        List<Receiver> all = receiverDao.getAll();
        if (all == null || all.size() == 0) {
            bot.execute(new DeleteMessage(chatId, messagePrevReport));
            bot.execute(new SendMessage(chatId, "заявки отсутствуют"));
            return;
        }

    }

    private void            addInfo(List<List<String>> reports, int rowIndex) {
        int cellIndex;
        for (List<String> report : reports) {
            firstSheet.createRow(++rowIndex);
            insertToRow(rowIndex, report, style);
        }
        for (int index = 0; index < 9; index++) {
            firstSheet.autoSizeColumn(index);
        }
    }

    private void            addSecondInfo(List<List<String>> reports, int rowIndex) {
        for (List<String> report : reports) {
            secondSheet.createRow(++rowIndex);
            insertSecondToRow(rowIndex, report, style);
        }
        for (int index = 0; index < 6; index++) {
            secondSheet.autoSizeColumn(index);
        }
    }

    private void            createTitle(XSSFCellStyle styleTitle, int rowIndex, List<String> title) {
        firstSheet.createRow(rowIndex);
        insertToRow(rowIndex, title, styleTitle);
    }

    private void            createSecondTitle(XSSFCellStyle styleTitle, int rowIndex, List<String> title) {
        secondSheet.createRow(rowIndex);
        insertSecondToRow(rowIndex, title, styleTitle);
    }

    private void            insertToRow(int row, List<String> cellValues, CellStyle cellStyle) {
        int cellIndex = 0;
        for (String cellValue : cellValues) {
            addCellValue(row, cellIndex++, cellValue, cellStyle);
        }
    }

    private void            insertSecondToRow(int row, List<String> cellValues, CellStyle cellStyle) {
        int cellIndex = 0;
        for (String cellValue : cellValues) {
            addSecondCellValue(row, cellIndex++, cellValue, cellStyle);
        }
    }

    private void            addCellValue(int rowIndex, int cellIndex, String cellValue, CellStyle cellStyle) {
        firstSheet.getRow(rowIndex).createCell(cellIndex).setCellValue(getString(cellValue));
        firstSheet.getRow(rowIndex).getCell(cellIndex).setCellStyle(cellStyle);
    }

    private void            addSecondCellValue(int rowIndex, int cellIndex, String cellValue, CellStyle cellStyle) {
        secondSheet.getRow(rowIndex).createCell(cellIndex).setCellValue(getString(cellValue));
        secondSheet.getRow(rowIndex).getCell(cellIndex).setCellStyle(cellStyle);
    }

    private String          getString(String nullable) {
        if (nullable == null) return "";
        return nullable;
    }

    private XSSFCellStyle   setStyle(XSSFWorkbook workbook, BorderStyle thin, short black, XSSFCellStyle style) {
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

    private void            sendFile(long chatId, DefaultAbsSender bot)                                throws TelegramApiException, IOException {
        String fileName = "Благотворительная помощь.xlsx";
        String path = "C:\\" + fileName;
        path += new Date().getTime();
        try (FileOutputStream stream = new FileOutputStream(path)) {
            workbook.write(stream);
        } catch (IOException e) {
            log.error("Can't send file error: ", e);
        }
        sendFile(chatId, bot, fileName, path);
    }

    private void            sendFile(long chatId, DefaultAbsSender bot, String fileName, String path)  throws TelegramApiException, IOException {
        File file = new File(path);
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            bot.execute(new SendDocument().setChatId(chatId).setDocument(fileName, fileInputStream));
        }
        file.delete();
    }
}
