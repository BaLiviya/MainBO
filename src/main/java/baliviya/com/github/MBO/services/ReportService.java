package baliviya.com.github.MBO.services;

import baliviya.com.github.MBO.dao.DaoFactory;
import baliviya.com.github.MBO.dao.impl.*;
import baliviya.com.github.MBO.entity.custom.Employee;
import baliviya.com.github.MBO.entity.custom.Task;
import baliviya.com.github.MBO.entity.custom.TaskArchive;
import baliviya.com.github.MBO.utils.Const;
import baliviya.com.github.MBO.utils.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

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
public class ReportService {

    private XSSFWorkbook     workbook       = new XSSFWorkbook();
    private Sheet            sheets;
    private XSSFCellStyle    style          = workbook.createCellStyle();
    private DefaultAbsSender bot;
    private DaoFactory       factory        = DaoFactory.getInstance();
    private TaskDao          taskDao        = factory.getTaskDao();
    private GroupDao         groupDao       = factory.getGroupDao();
    private UserDao          userDao        = factory.getUserDao();
    private CategoryDao      categoryDao    = factory.getCategoryDao();
    private EmployeeDao      employeeDao    = factory.getEmployeeDao();
    private TaskArchiveDao   taskArchiveDao = factory.getTaskArchiveDao();

    public void sendReports(long chatId, DefaultAbsSender bot, Date dateBegin, Date dateEnd, int groupId, int messagePreviewReport) {
        this.bot = bot;
        try {
            sendReport(chatId, dateBegin, dateEnd, groupId, messagePreviewReport);
        } catch (Exception e) {
            log.error("Can't create/send report", e);
            try {
                bot.execute(new SendMessage(chatId, "Ошибка при создании отчета"));
            } catch (TelegramApiException ex) {
                log.error("Can't send message", ex);
            }
        }
    }

    private void sendReport(long chatId, Date dateBegin, Date dateEnd, int idGroup, int messagePreviewReport) throws TelegramApiException, IOException {
        sheets = workbook.createSheet("Статистика");
        List<Task> tasks = taskDao.getTaskToTime(idGroup, dateBegin, dateEnd);
        String group = groupDao.getGroupToId(idGroup).getNames();
        if (tasks == null || tasks.size() == 0) {
            bot.execute(new DeleteMessage(chatId, messagePreviewReport));
            bot.execute(new SendMessage(chatId, "За выбранный период заявки отсутствуют"));
            return;
        }
        int done = taskDao.getTaskCountTime(idGroup, 1, dateBegin, dateEnd);
        int doing = taskDao.getTaskCountTime(idGroup, 3, dateBegin, dateEnd);
        int total = done + doing;
        BorderStyle thin = BorderStyle.THIN;
        short black = IndexedColors.BLACK.getIndex();
        XSSFCellStyle styleTitle = setStyle(workbook, thin, black, style);
        int rowIndexForTitle = 1;
        createTitle(styleTitle,rowIndexForTitle, Arrays.asList("Группа;Выполнено;В процессе; Общее".split(Const.SPLIT)));
        int titleId = 2;
        sheets.createRow(titleId);
        addCellValue(titleId,0, group, style);
        addCellValue(titleId, 1, String.valueOf(done), style);
        addCellValue(titleId, 2, String.valueOf(doing), style);
        addCellValue(titleId, 3, String.valueOf(total), style);
        int rowIndexForSecondTitle = 4;
        createTitle(styleTitle, rowIndexForSecondTitle, Arrays.asList("#;Житель;Тег;Текст;Дата;Время;Исполнитель;Статус;Время/Ответ исполнителя".split(Const.SPLIT)));
        List<List<String>> info = tasks.stream().map(x -> {
            StringBuilder employeeNames = new StringBuilder();
            for (Employee employee : employeeDao.getList(x.getDistrict())) {
                employeeNames.append(userDao.getUserByChatId(employee.getEmployeeId()).getFullName()).append(",");
            }
            List<String> list = new ArrayList<>();
            list.add(String.valueOf(x.getId()));
            list.add(x.getPeopleName() + " (" + userDao.getUserByChatId(x.getPeopleId()).getFullName() + " " + userDao.getUserByChatId(x.getPeopleId()).getPhone() + ")");
            list.add(categoryDao.getCategory(x.getDistrict()).getName());
            list.add(x.getTaskText());
            list.add(DateUtil.getDayDate(x.getDateBegin()));
            list.add(DateUtil.getTimeDate(x.getDateBegin()));
            list.add(employeeNames.toString());
            if (x.getStatusId() == 3) {
                list.add("В процессе");
            } else if (x.getStatusId() == 1) {
                list.add("Выполненно");
            }
            StringBuilder stringBuilder = new StringBuilder();
            for (TaskArchive taskArchive : taskArchiveDao.getTaskArchive(x.getId())) {
                stringBuilder.append(taskArchive.getDate()).append(" ").append(taskArchive.getText()).append("\n");
            }
            list.add(stringBuilder.toString());
            return list;
        }).collect(Collectors.toList());
        addInfo(info, rowIndexForSecondTitle);
        sendFile(chatId, dateBegin, dateEnd);
    }

    private void addInfo(List<List<String>> reports, int rowIndex) {
//        int cellIndex;
        for (List<String> stringList : reports) {
            sheets.createRow(++rowIndex);
            insertToRow(rowIndex, stringList, style);
        }
    }

    private void createTitle(XSSFCellStyle styleTitle, int rowIndex, List<String> title) {
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 10);
        font.setFontName("Arial");
        font.setItalic(false);
        styleTitle.setFont(font);
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
        sheets.getRow(rowIndex).getCell(cellIndex).setCellStyle(cellStyle);
    }

    private String getString(String nullable) {
        if (nullable == null) {
            return "";
        }
        return nullable;
    }

    private XSSFCellStyle setStyle(XSSFWorkbook wb, BorderStyle thin, short black, XSSFCellStyle style) {
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
        return styleTitle;
    }

    private void sendFile(long chatId, Date dateBegin, Date dateEnd) throws IOException, TelegramApiException {
        String fileName = DateUtil.getDayDate(dateBegin) + " - " + DateUtil.getDayDate(dateEnd) + ".xlsx";
        String path = "C:\\" + fileName;
        path += new Date().getTime();
        try(FileOutputStream fileOutputStream = new FileOutputStream(path)) {
            workbook.write(fileOutputStream);
        }
        sendFile(chatId, fileName, path);
    }

    private void sendFile(long chatId, String fileName, String path) throws TelegramApiException, IOException {
        File file = new File(path);
        try(FileInputStream fileInputStream = new FileInputStream(file)) {
            bot.execute(new SendDocument().setChatId(chatId).setDocument(fileName, fileInputStream));
        }
        file.delete();
    }
}
