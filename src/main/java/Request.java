import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Request {
    private static String token;
    private static String session_key = null;
    private static final List<String> idList = new ArrayList<>();

    Study study = new Study();

    public void login(String URL,
                      String LOGIN,
                      String PASSWORD) {
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("email", LOGIN);
            requestBody.put("password", PASSWORD);
        } catch (Exception e) {
            e.printStackTrace();
        }
        RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/json");
        request.body(requestBody.toString());
        token = request.post(URL)
                .then().extract().response()
                .path("token").toString();
        System.out.println("Login is DONE \n Token is: \n" + token);
    }

    public void createFiles(String mode) {
        System.out.println("Create Files is Started");
        Path rootFolder = Path.of("/Users/denis/Desktop/Care Mentor AI/Тестовые данные/СT грудной клетки/CT | Covid-19/root/covid");
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
        System.out.println("Create Files is DONE");
    }

    private void uploadFile(String mode, File file) {
//        System.out.println("Upload is Started");
//        System.out.println(session_key);
        if (session_key == null) {
            session_key = RestAssured.given()
                    .multiPart("file", file, "multipart/form-data")
                    .post("https://test-box-webshow.cmai.tech/api/v2/records/file?mode=" + mode + "&token=" + token)
                    .then().extract().response().path("_session_key").toString();
//            System.out.println(session_key);
        } else {
            RestAssured.given()
                    .multiPart("file", file, "multipart/form-data")
                    .multiPart("_session_key", session_key)
                    .post("https://test-box-webshow.cmai.tech/api/v2/records/file?mode=" + mode + "&token=" + token);
//            System.out.println(session_key);
//            System.out.println("Upload is Done");
        }
    }

    private void createRecord(String mode) {
        System.out.println("Create Record is Started");
        String id = RestAssured.given().multiPart("mode", mode)
                .multiPart("token", token)
                .multiPart("_session_key", session_key)
                .post("https://test-box-webshow.cmai.tech/api/v2/records?mode=" + mode +
                        "&token=" + token + "&_session_key=" + session_key)
                .then().extract().body().path("id").toString();
        idList.add(id);
        System.out.println("Create Record is Done, ID = " + id);
    }

    protected void getRecord() {
        LinkedHashMap<?, ?> result_localized;
        if (idList.isEmpty()) {
            try {
                System.out.println("getRecord: List is Empty");
                System.out.println("Sleep 30 sec");
                Thread.sleep(30000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            getRecord();
        } else {
            System.out.println("getRecord: List is NOT Empty");
            for (String id : idList) {
                Response response = RestAssured.given()
                        .multiPart("token", token)
                        .get("https://test-box-webshow.cmai.tech/api/v2/records/" + id +
                                "?token=" + token);
                String status = response.getBody().path("status").toString();
                System.out.println("Status is " + status);
                if (status.equals("2")) {
                    System.out.println("Sleep 20 sec");
                    try {
                        Thread.sleep(20000);
                        getRecord();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else if (status.equals("10")) {
                    System.out.println("Status is 10");
                    study.setId(response.body().path("id").toString());
                    result_localized = response.body().path("result_localized");
                    study.setIsHealthy(result_localized.get("is_healthy").toString());

                    // prob после теста закоментировать, его вырежут из ответа
                    study.setProb(result_localized.get("prob").toString());
                    study.setStatus(response.body().path("status").toString());
                    study.setStatusText(response.body().path("status_text"));
                    System.out.println(study.toString());
                    idList.remove(id);
                    ExcelTable.fillTable(study);
                    getRecord();
                } else {
                    study.setId(response.body().path("id").toString());
                    study.setStatus(response.body().path("status").toString());
                    study.setStatusText(response.body().path("status_text"));
                    idList.remove(id);
                    ExcelTable.fillTable(study);
                }
            }
        }
    }


}