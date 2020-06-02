package baliviya.com.github.MBO.command.impl;

import baliviya.com.github.MBO.command.Command;
import baliviya.com.github.MBO.entity.standart.User;
import baliviya.com.github.MBO.utils.Const;
import baliviya.com.github.MBO.utils.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

@Slf4j
public class id030_ShowUsers extends Command {

    private List<User> allUser;
    private int        count;
    private int        messagePreviewReport;

    @Override
    public boolean execute() throws TelegramApiException, SQLException {
        if (!isAdmin()) {
            sendMessage(Const.NO_ACCESS);
            return EXIT;
        }
        if (hasMessageText()) {
            count = userDao.count();
            allUser = userDao.getAll();
            if (count == 0) {
                sendMessage("Зарегистрированных пользователей нет.");
                return EXIT;
            }
            messagePreviewReport = sendMessage(String.format("Список подготавливается. Всего пользователей %d.", count));
            new Thread(() -> {
                try {
                    sendReport();
                } catch (Exception e) {
                    log.error("Can't send report", e);
                    try {
                        sendMessage("Ошибка отправки списка");
                    } catch (Exception ex) {
                        log.error("Can't send message", ex);
                    }
                }
            }).start();
        }
        return COMEBACK;
    }

    private void sendReport() throws TelegramApiException {
        int total = count;
        XSSFWorkbook wb = new XSSFWorkbook();
        Sheet sheets = wb.createSheet("Пользователи");
        // -------------------------Стиль ячеек-------------------------
        BorderStyle thin = BorderStyle.THIN;
        short black = IndexedColors.BLACK.getIndex();
        XSSFCellStyle style = wb.createCellStyle();
        style.setWrapText(true);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.BLUE.getIndex());
        style.setBorderTop(thin);
        style.setBorderBottom(thin);
        style.setBorderRight(thin);
        style.setBorderLeft(thin);
        style.setTopBorderColor(black);
        style.setRightBorderColor(black);
        style.setBottomBorderColor(black);
        style.setLeftBorderColor(black);
        BorderStyle tittle = BorderStyle.MEDIUM;
        XSSFCellStyle styleTitle = wb.createCellStyle();
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
        style.setFillForegroundColor(new XSSFColor(new java.awt.Color(0, 52, 94)));
        Sheet sheet = wb.getSheetAt(0);
        //--------------------------------------------------------------------
        int rowIndex = 0;
        int CellIndex = 0;
        sheets.createRow(rowIndex).createCell(CellIndex).setCellValue("Регистрационные данные");
        sheet.getRow(rowIndex).getCell(CellIndex).setCellStyle(styleTitle);
        sheets.getRow(rowIndex).createCell(++CellIndex).setCellValue("Телефон");
        sheet.getRow(rowIndex).getCell(CellIndex).setCellStyle(styleTitle);
        sheets.getRow(rowIndex).createCell(++CellIndex).setCellValue("Данные telegram");
        sheet.getRow(rowIndex).getCell(CellIndex).setCellStyle(styleTitle);
        for (User entity : allUser) {
            CellIndex = 0;
            sheets.createRow(++rowIndex).createCell(CellIndex).setCellValue(entity.getFullName());
            sheet.getRow(rowIndex).getCell(CellIndex).setCellStyle(style);
            sheets.getRow(rowIndex).createCell(++CellIndex).setCellValue(entity.getPhone());
            sheet.getRow(rowIndex).getCell(CellIndex).setCellStyle(style);
            sheets.getRow(rowIndex).createCell(++CellIndex).setCellValue(entity.getUserName());
            sheet.getRow(rowIndex).getCell(CellIndex).setCellStyle(style);
        }
        String[] splitWidth = "13200;13200;13200".split(";");
        for (int i = 0; i < splitWidth.length; i++) {
            if (splitWidth[i].equalsIgnoreCase("auto")) {
                sheets.autoSizeColumn(i);
            } else {
                int size = 0;
                try {
                    size = Integer.parseInt(splitWidth[i].replaceAll("[^0-9]", ""));
                } catch (NumberFormatException e) {
                    log.warn("Error in message № 309 - {}", splitWidth[i]);
                }
                if (size > 0) {
                    sheets.setColumnWidth(i, size);
                }
            }
        }
        String filename = String.format("List users %s.xlsx", DateUtil.getDayDate(new Date()));
        deleteMessage(messagePreviewReport);
        bot.execute(new SendDocument().setChatId(chatId).setDocument(filename, getInputStream(wb)));
    }

    private InputStream getInputStream(XSSFWorkbook workbook) {
        ByteArrayOutputStream tables = new ByteArrayOutputStream();
        try {
            workbook.write(tables);
        } catch (IOException e) {
            log.error("Can't write table to wb, case: {}", e);
        }
        return new ByteArrayInputStream(tables.toByteArray());
    }
}
