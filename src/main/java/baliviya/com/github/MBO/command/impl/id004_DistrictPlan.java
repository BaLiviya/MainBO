package baliviya.com.github.MBO.command.impl;

import baliviya.com.github.MBO.command.Command;
import baliviya.com.github.MBO.entity.enums.WaitingType;
import baliviya.com.github.MBO.utils.ButtonsLeaf;
import baliviya.com.github.MBO.utils.Const;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class id004_DistrictPlan extends Command {

    private List<String>    list;
    private ButtonsLeaf     buttonsLeaf;
    private int             deleteMessageId;
    private String          pathName;
    private String          fileId;

    @Override
    public boolean  execute()       throws TelegramApiException, SQLException {
        if (!isAdmin()) {
            sendMessage(Const.NO_ACCESS);
            return EXIT;
        }
        switch (waitingType) {
            case START:
                deleteMessage(updateMessageId);
                deleteMessageId = getDistrict();
                waitingType     = WaitingType.SET_DISTRICT;
                return COMEBACK;
            case SET_DISTRICT:
                deleteMessage(updateMessageId);
                deleteMessage(deleteMessageId);
                if (hasCallbackQuery()) {
                    switch (list.get(Integer.parseInt(updateMessageText))) {
                        case "Медеуский район" :
                            pathName = "MEDBO.xlsx";
                            break;
                        case "Турксибский район":
                            pathName = "TDBO.xlsx";
                            break;
                        case "Наурызбайский район" :
                            pathName = "NDBO.xlsx";
                            break;
                        case "Жетисуский район":
                            pathName = "ZHDBO.xlsx";
                            break;
                        case "Ауезовский район":
                            pathName = "AUDBO.xlsx";
                            break;
                        case "Алатауский район":
                            pathName = "ALDBO.xlsx";
                            break;
                        case "Бостандыкский район":
                            pathName = "BDBO.xlsx";
                            break;
                        case "Алмалинский район":
                            pathName = "ALMLDBO.xlsx";
                            break;

                    }
                    sendMessage("Отправьте файл");
                    waitingType     = WaitingType.UPDATE_FILE;
                    return COMEBACK;
                } else {
                    deleteMessageId = getDistrict();
                }
                return COMEBACK;
            case UPDATE_FILE:
                deleteMessage(updateMessageId);
                deleteMessage(deleteMessageId);
                if (hasDocument()) {
                    fileId              = update.getMessage().getDocument().getFileId();
                    File direct         = new File(factory.getPropertiesDao().getPropertiesValue(3));
                    File file           = bot.downloadFile(uploadFile(fileId));
                    File file1          = new File(direct, pathName);
                    try(FileInputStream fileInputStream = new FileInputStream(file); FileOutputStream fileOutputStream = new FileOutputStream(file1)) {
                        byte[] buffer   = new byte[1024];
                        int length;
                        while ((length  = fileInputStream.read(buffer)) > 0) {
                            fileOutputStream.write(buffer, 0, length);
                        }
                    } catch (IOException e) { e.printStackTrace(); }
                    sendMessage("Завершено!");
                    return EXIT;
//                    bot.downloadFile(uploadFile(fileId));

                }
                return COMEBACK;
        }
        return EXIT;
    }

    private int     getDistrict()   throws TelegramApiException {
        list = new ArrayList<>();
        Arrays.asList(getText(Const.DISTRICT_TYPE_MESSAGE).split(Const.SPLIT)).forEach(e -> list.add(e));
        buttonsLeaf = new ButtonsLeaf(list);
        return toDeleteKeyboard(sendMessageWithKeyboard(getText(Const.SELECT_DISTRICT_MESSAGE), buttonsLeaf.getListButton()));
    }

    private String  uploadFile(String fileId) {
        Objects.requireNonNull(fileId);
        GetFile getFile = new GetFile();
        getFile.setFileId(fileId);
        try {
            org.telegram.telegrambots.meta.api.objects.File file = bot.execute(getFile);
            return file.getFilePath();
        } catch (TelegramApiException e) {
            throw new IllegalStateException(e);
        }
    }
}

