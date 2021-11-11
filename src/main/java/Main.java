import java.util.concurrent.ExecutorService;

public class Main {

    public static void main(String[] args) {
        Request request = new Request();
        request.login("https://test-box-webshow.cmai.tech/api/v2/login", "qa@cmai.team", "wkzgNJhXShDhNdfmyshEkpzVeMPxGc");
        request.createFiles("ct_lung_screening_covid");
    }
}
