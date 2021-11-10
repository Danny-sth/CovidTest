import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Request {
    private static String token;
    private static String session_key = null;
    private static List<String> idList = new ArrayList<>();

    Study study;

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
        Path rootFolder = Path.of("/home/danny/covid");
        try {
            List<Path> folders = Files.walk(rootFolder)
                    .filter(Files::isDirectory).collect(Collectors.toList());
            for (Path folder : folders) {
                List<Path> filesPaths = Files.walk(folder)
                        .filter(Files::isRegularFile).collect(Collectors.toList());
                for (Path filePath : filesPaths) {
                    File file = new File(filePath.toString());
                    uploadFile(mode, file);
                }
            }
            createRecord(mode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void uploadFile(String mode, File file) {
        if (session_key == null) {
            session_key = RestAssured.given()
                    .multiPart("file", file, "multipart/form-data")
                    .post("https://test-box-webshow.cmai.tech/api/v2/records/file?mode=" + mode + "&token=" + token)
                    .then().extract().body().path("_session_key").toString();
        } else {
            RestAssured.given()
                    .multiPart("file", file, "multipart/form-data")
                    .multiPart("_session_key", session_key)
                    .post("https://test-box-webshow.cmai.tech/api/v2/records/file?mode=" + mode + "&token=" + token)
                    .then().extract().body().path("_session_key").toString();
        }
    }

    private void createRecord(String mode) {
        String id = RestAssured.given().multiPart("mode", mode)
                .multiPart("token", token)
                .multiPart("_session_key", session_key)
                .post("https://test-box-webshow.cmai.tech/api/v2/records?mode=" + mode +
                        "&token=" + token + "&_session_key=" + session_key)
                .then().extract().body().path("id").toString();
        idList.add(id);
    }

    protected void getRecord() {
        if (idList.isEmpty()) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("getRecord: List is Empty");
        } else {
            System.out.println("getRecord: List is NOT Empty");
            for (String id : idList) {
                Response response = RestAssured.given()
                        .multiPart("token", Request.token)
                        .post("https://test-box-webshow.cmai.tech/api/v2/records/" + id +
                                "?token=" + Request.token);
                String status = response.body().path("status");
                System.out.println("Status is " + status);
                if (status == "2") {
                    try {
                        Thread.sleep(20000);
                        getRecord();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else if (status == "10") {
                    study.setId(response.body().path("id"));
                    study.setIsHealthy(response.body().path("is_healthy"));

                    // prob после теста закоментировать, его вырежут из ответа
                    study.setProb(response.body().path("prob"));
                    study.setStatus(response.body().path("status"));
                    study.setStatusText(response.body().path("status_text"));
                    System.out.println(study.toString());
                    idList.remove(id);
                    ExcelTable.fillTable(study);
                    getRecord();
                } else {
                    study.setId(response.body().path("id"));
                    study.setStatus(response.body().path("status"));
                    study.setStatusText(response.body().path("status_text"));
                    idList.remove(id);
                    ExcelTable.fillTable(study);
                }
            }
        }
    }


}