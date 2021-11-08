import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.stream.Stream;

public class Request {
    private static String token;

    @Test
    public void login(String URL,
                      String LOGIN,
                      String PASSWORD) {
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("email", LOGIN);
            requestBody.put("password", PASSWORD);
        } catch (Exception e) {
        }
        RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/json");
        request.body(requestBody.toString());
        token = request.post(URL)
                .then().extract().response()
                .path("token").toString();
    }

    public void createFiles(String mode) {
        Path rootFolder = Path.of("/Users/denis/Desktop/Care Mentor AI/Тестовые данные/СT грудной клетки/CT | Covid-19/covid");
        try {
            Stream<Path> folders = Files.walk(rootFolder).filter(Files::isDirectory);
            folders.forEach(folder -> {
                try {
                    Files.walk(folder).filter(Files::isRegularFile).forEach(this::fooMethod);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        Stream<Path> filesStream;
        try {
            filesStream = Files.walk(Paths
                    .get());
            RestAssured.given().multiPart("file[]", filesStream)
                    .when().post("https://test-box-webshow.cmai.tech/api/v2/records/file?mode=" + mode + "&token=" + token)
                    .then().extract().body().asString();
        } catch (IOException e) {
        }
    }

    List<Long> toCheck = new ArrayList<>();

    private void fooMethod(Path pathToFile) {
        beforeLogic();
        long id = upload();
        toCheck.add(id);
        ExecutorService
    }

    private void beforeLogic() {
    }

    private long upload() {
        return 0;
    }

    private void afterLogic(long id) {

    }

}