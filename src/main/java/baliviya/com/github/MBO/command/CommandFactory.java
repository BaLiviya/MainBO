package baliviya.com.github.MBO.command;

import baliviya.com.github.MBO.command.impl.*;
import baliviya.com.github.MBO.exceptions.NotRealizedMethodException;

public class CommandFactory {

    public static Command getCommand(long id) {
        Command result = getCommandWithoutReflection((int) id);
        if (result == null) throw new NotRealizedMethodException("Not realized for type: " + id);
        return result;
    }

    private static Command getCommandWithoutReflection(int id) {
        switch (id) {
            case 1:
                return new id001_ShowInfo();
            case 2:
                return new id002_SelectionLanguage();
            case 3:
                return new id003_Suggestion();
            case 4:
                return new id004_DistrictPlan();
            case 5:
                return new id005_Reminder();
            case 6:
//                return new id006_Training();
            case 7:
//                return new id007_SupportBusinessProject();
            case 8:
//                return new id008_Service();
            case 9:
//                return new id009_ShowEvent();
            case 10:
                return new id010_Survey();
            case 11:
                return new id011_ShowAdminInfo();
            case 12:
                return new id012_EditMenu();
            case 13:
                return new id013_AddSurvey();
            case 14:
                return new id014_EditSurvey();
            case 15:
                return new id015_EditAdmin();
            case 16:
                return new id016_ReportSuggestion();
            case 17:
                return new id017_ReportSurvey();
            case 18:
//                return new id018_ReportProfile();
            case 19:
//                return new id019_EditEvent();
            case 20:
//                return new id020_ReportService();
            case 21:
                return new id021_GroupManager();
            case 22:
                return new id022_RegistrationUser();
            case 23:
                return new id023_Complaints();
            case 24:
                return new id024_CreateDonate();
            case 25:
                return new id025_CreateReceiver();
            case 26:
                return new id026_StatisticGroup();
            case 27:
                return new id027_StatisticPeople();
            case 28:
                return new id028_Statistic();
            case 29:
                return new id029_ReportComplaint();
            case 30:
                return new id030_ShowUsers();
            case 31:
                return new id031_FindUser();
            case 32:
                return new id032_DonateReport();
        }
        return null;
    }
}
