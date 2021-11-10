import java.util.concurrent.ExecutorService;

public class Main {

    public static void main(String[] args) {
//        Thread getRecordThread =
//                new Thread(new GetRecordThread(), "Get Record Thread");
//        System.out.println(getRecordThread.getName() + " Is Started");
        Request request = new Request();
        request.login("https://test-box-webshow.cmai.tech/api/v2/login", "qa@cmai.team", "wkzgNJhXShDhNdfmyshEkpzVeMPxGc");
//        getRecordThread.start();
        request.createFiles("ct_lung_screening_covid");
    }
}
