public class Env {

    public static String DOMEN = "";
    public static String LOGIN = "";
    public static String PASSWORD = "";
    public static String MODE = "";
    public static String ROOT_FOLDER = "/home/danny/covid/";
    public static String FILE_NAME = "table.xlsx";
    public static final String FILE_PATH = ROOT_FOLDER + FILE_NAME;


    // endpoints
    public static final String loginEndpoint = "https://" + DOMEN + "/api/v2/login";
    public static final String createFilesEndpoint = "https://" + DOMEN + "/api/v2/records/file";
    public static final String createRecordEndpoint = "https://" + DOMEN + "/api/v2/records";
    public static final String getEndpoint = "https://" + DOMEN + "/api/v2/records/";

}
