package baliviya.com.github.MBO.services;

import baliviya.com.github.MBO.dao.DaoFactory;
import baliviya.com.github.MBO.dao.impl.RegistrationHandlingDao;
import baliviya.com.github.MBO.entity.custom.RegistrationHandling;
import baliviya.com.github.MBO.utils.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.*;
import java.util.Date;
import java.util.List;

@Slf4j
public class ServiceReportService {

    private DaoFactory             daoFactory               = DaoFactory.getInstance();
    private RegistrationHandlingDao registrationHandlingDao = daoFactory.getRegistrationHandlingDao();
    private XSSFWorkbook           workbook                 = new XSSFWorkbook();
    private XSSFCellStyle          style                    = workbook.createCellStyle();
    private XSSFWorkbook           originWorkbook;
    private Sheet                  originSheet;
    private Sheet                  secondOriginSheet;
    private Sheet                  sheet;
    private Sheet                  secondSheet;


    public void sendServiceReport(long chatId, DefaultAbsSender bot, Date dateBegin, Date dateEnd, int messagePrevReport) throws TelegramApiException {
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

    private void sendReport(long chatId, DefaultAbsSender bot, Date dateBegin, Date dateEnd, int messagePrevReport) throws IOException, TelegramApiException {
        try {
            originWorkbook = new XSSFWorkbook(new FileInputStream(new File("/home/bal/mainFile.xlsx")));
        } catch (Exception e) {
            log.error("Can't read file, error: ", e);
        }
        List<RegistrationHandling> allCoursesByTime = registrationHandlingDao.getAllCoursesByTime(dateBegin, dateEnd);
        createFirstTitle();
        createSecondTitle();
        if (allCoursesByTime == null || allCoursesByTime.size() == 0) {

        } else {
            int rowIndex = 6;
            int numberCell = 3;
            int numberCellReg = 2;
            int countReg = 0;
            for (int index = 1; index < 21; index++) {
                int count = 0;
                for (RegistrationHandling handling : allCoursesByTime) {
                    if (handling.getIdHandling() == index) {
                        count++;
                    }
                }
                setTitleValue(sheet.getRow(rowIndex), numberCell, String.valueOf(count), rowIndex);
                setTitleValue(sheet.getRow(rowIndex), numberCellReg, String.valueOf(countReg), rowIndex);
                if (rowIndex == 8 || rowIndex == 12 || rowIndex == 15 || rowIndex == 19 || rowIndex == 25 || rowIndex == 28) {
                    rowIndex++;
                }
                rowIndex++;
            }
        }
        addInfo();
        sendFile(chatId, bot, dateBegin, dateEnd);
    }

    private void createFirstTitle() {
        originSheet = originWorkbook.getSheetAt(0);
        sheet = workbook.createSheet(originSheet.getSheetName());
        for (int indexRow = 0; indexRow < originSheet.getLastRowNum() + 1; indexRow++) {
            Row originRow = originSheet.getRow(indexRow);
            Row row = sheet.createRow(indexRow);
            if (indexRow == 1) {
                row.setHeight((short)1600);
            }
            for (int indexCell = 0; indexCell < 4; indexCell++) {
                setTitleValue(row, indexCell, getTitleValue(originRow, indexCell), indexRow);
            }
        }
    }

    private void createSecondTitle() {
        secondOriginSheet = originWorkbook.getSheetAt(3);
        secondSheet = workbook.createSheet(secondOriginSheet.getSheetName());
        for (int indexRow = 0; indexRow < secondOriginSheet.getLastRowNum() + 1; indexRow++) {
            Row originRow = secondOriginSheet.getRow(indexRow);
            Row row = secondSheet.createRow(indexRow);
            if (indexRow == 2 || indexRow == 3) {
                row.setHeight((short) 550);
            }
            for (int indexCell = 0; indexCell < 4; indexCell++) {
                setSecondTitleValue(row, indexCell, getTitleValue(originRow, indexCell), indexRow);
            }
        }
    }

    private void addInfo() {
        int cellIndex = 0;
        sheet.autoSizeColumn(cellIndex++);
        sheet.setColumnWidth(cellIndex++, 22000);
        sheet.setColumnWidth(cellIndex++, 4000);
        sheet.setColumnWidth(cellIndex,   4000);
        sheet.addMergedRegion(new CellRangeAddress(1,1,1,3));
        sheet.addMergedRegion(new CellRangeAddress(3,3,2,3));
        for (cellIndex = 0; cellIndex < 4; cellIndex++) {
            secondSheet.autoSizeColumn(cellIndex);
        }
        secondSheet.addMergedRegion(new CellRangeAddress(2,2,2,3));
    }

    private void setTitleValue(Row row, int numberCell, String cellValue, int rowIndex) {
        Cell cell = row.createCell(numberCell);
        if (rowIndex == 1 && numberCell == 1) {
            cell.setCellStyle(setBoldStyle(14));
        } else if (rowIndex >= 3 && numberCell == 2 || rowIndex >= 3 && numberCell == 3) {
            cell.setCellStyle(setStyleYellowBackground());
        } else if (rowIndex ==  4 && numberCell == 0 || rowIndex ==  4 && numberCell == 1
                || rowIndex == 32 && numberCell == 0 || rowIndex == 32 && numberCell == 1
                || rowIndex == 37 && numberCell == 0 || rowIndex == 37 && numberCell == 1) {
            cell.setCellStyle(setStyleGreyBackground());
        } else if (rowIndex ==  5 && numberCell == 0 || rowIndex ==  5 && numberCell == 1
                || rowIndex ==  3 && numberCell == 0 || rowIndex ==  3 && numberCell == 1
                || rowIndex ==  9 && numberCell == 0 || rowIndex ==  9 && numberCell == 1
                || rowIndex == 13 && numberCell == 0 || rowIndex == 13 && numberCell == 1
                || rowIndex == 16 && numberCell == 0 || rowIndex == 16 && numberCell == 1
                || rowIndex == 20 && numberCell == 0 || rowIndex == 20 && numberCell == 1
                || rowIndex == 26 && numberCell == 0 || rowIndex == 26 && numberCell == 1
                || rowIndex == 29 && numberCell == 0 || rowIndex == 29 && numberCell == 1
                || rowIndex == 41 && numberCell == 0 || rowIndex == 41 && numberCell == 1) {
            cell.setCellStyle(setStyleBold());
        } else {
            cell.setCellStyle(setStyle());
        }
        cell.setCellValue(cellValue);
    }

    private void setSecondTitleValue(Row row, int numberCell, String cellValue, int rowIndex) {
        Cell cell = row.createCell(numberCell);
        if (rowIndex == 0 && numberCell == 1) {
            cell.setCellStyle(setBoldStyle(14));
        } else if (rowIndex == 2 && numberCell == 1 || rowIndex == 3 && numberCell == 0 || rowIndex == 3 && numberCell == 1 ||
                rowIndex == 5 && numberCell == 0 || rowIndex == 5 && numberCell == 1 ||
                rowIndex == 8 && numberCell == 0 || rowIndex == 8 && numberCell == 1 ||
                rowIndex == 27 && numberCell == 1) {
            cell.setCellStyle(setStyleBold());
        } else if (rowIndex >= 2 && numberCell == 2 || rowIndex >= 2 && numberCell == 3) {
            cell.setCellStyle(setStyleYellowBackground());
        } else if (rowIndex == 4 && numberCell == 0 || rowIndex == 4 && numberCell == 1 ||
                rowIndex == 14 && numberCell == 0 || rowIndex == 14 && numberCell == 1 ||
                rowIndex == 23 && numberCell == 0 || rowIndex == 23 && numberCell == 1) {
            cell.setCellStyle(setStyleGreyBackground());
        } else {
            cell.setCellStyle(setStyle());
        }
        cell.setCellValue(cellValue);
    }

    private String getTitleValue(Row row, int numberCell) {
        Cell cell = row.getCell(numberCell);
        cell.setCellType(CellType.STRING);
        return cell.getStringCellValue();
    }

    private XSSFCellStyle setStyleYellowBackground() {
        XSSFCellStyle cellStyle = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        BorderStyle title = BorderStyle.THIN;
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setBorderTop(title);
        cellStyle.setBorderBottom(title);
        cellStyle.setBorderRight(title);
        cellStyle.setBorderLeft(title);
        cellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
        cellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
        cellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        cellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        cellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        font.setFontName("Times New Roman");
        font.setFontHeight(12);
        font.setBold(true);
        cellStyle.setFont(font);
        return cellStyle;
    }

    private XSSFCellStyle setStyleGreyBackground() {
        XSSFCellStyle cellStyle = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        BorderStyle title = BorderStyle.THIN;
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setBorderTop(title);
        cellStyle.setBorderBottom(title);
        cellStyle.setBorderRight(title);
        cellStyle.setBorderLeft(title);
        cellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
        cellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
        cellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        cellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        cellStyle.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.getIndex());
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        font.setFontName("Times New Roman");
        font.setFontHeight(12);
        font.setBold(true);
        cellStyle.setFont(font);
        return cellStyle;
    }

    private XSSFCellStyle setStyleBold() {
        XSSFCellStyle cellStyle = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        BorderStyle title = BorderStyle.THIN;
        cellStyle.setAlignment(HorizontalAlignment.LEFT);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setBorderTop(title);
        cellStyle.setBorderBottom(title);
        cellStyle.setBorderRight(title);
        cellStyle.setBorderLeft(title);
        cellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
        cellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
        cellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        cellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        font.setFontName("Times New Roman");
        font.setFontHeight(12);
        font.setBold(true);
        cellStyle.setFont(font);
        return cellStyle;
    }

    private XSSFCellStyle setStyle() {
        XSSFCellStyle cellStyle = workbook.createCellStyle();
        XSSFFont      font      = workbook.createFont();
        BorderStyle title = BorderStyle.THIN;
        cellStyle.setAlignment(HorizontalAlignment.LEFT);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setBorderTop(title);
        cellStyle.setBorderBottom(title);
        cellStyle.setBorderRight(title);
        cellStyle.setBorderLeft(title);
        cellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
        cellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
        cellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        cellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        font.setFontName("Times New Roman");
        font.setFontHeight(12);
        cellStyle.setFont(font);
        return cellStyle;
    }

    private XSSFCellStyle setBoldStyle(int fontSize) {
        XSSFCellStyle cellStyle = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        font.setFontName("Times New Roman");
        font.setFontHeight(fontSize);
        font.setBold(true);
        cellStyle.setFont(font);
        return cellStyle;
    }

    private void sendFile(long chatId, DefaultAbsSender bot, Date dateBegin, Date dateEnd) throws IOException, TelegramApiException {
        String fileName = "Болванка за: " + DateUtil.getDayDate(dateBegin) + " - " + DateUtil.getDayDate(dateEnd) + ".xlsx";
        String path = "C:\\" + fileName;
        path += new Date().getTime();
        try (FileOutputStream stream = new FileOutputStream(path)) {
            workbook.write(stream);
        } catch (IOException e) {
            log.error("Can't send file error: ", e);
        }
        sendFile(chatId, bot, fileName, path);
    }

    private void sendFile(long chatId, DefaultAbsSender bot, String fileName, String path) throws TelegramApiException, IOException {
        File file = new File(path);
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            bot.execute(new SendDocument().setChatId(chatId).setDocument(fileName, fileInputStream));
        }
        file.delete();
    }
}
