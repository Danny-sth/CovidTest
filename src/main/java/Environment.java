public class Environment {

    public static final String domen = "webshow.cmai.tech";
    public static final String LOGIN = "qa@cmai.team";
    public static final String PASSWORD = "wkzgNJhXShDhNdfmyshEkpzVeMPxGc";
    public static final String MODE = "ct_lung_screening_covid";
    public static final String rootFolder = "/Users/denis/Desktop/Care Mentor AI/Тестовые данные/СT грудной клетки/CT | Covid-19/root";
    public static final String excelFilePath = "/Users/denis/Desktop/Care Mentor AI/Тестовые данные/СT грудной клетки/CT | Covid-19/root/table.xlsx";


    // endpoints
    public static final String loginEndpoint = "https://" + domen + "/api/v2/login";
    public static final String createFilesEndpoint = "https://" + domen + "/api/v2/records/file";
    public static final String createRecordEndpoint = "https://" + domen + "/api/v2/records";
    public static final String getEndpoint = "https://" + domen + "/api/v2/records/";

}
