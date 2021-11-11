import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class Request {
    private static final List<String> idList = new CopyOnWriteArrayList<>();
    private static String token;
    private static String session_key = null;
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
        Path rootFolder = Path.of("/home/danny/covid");
        try {
            // FIXME try to remove rootFolder filter
            List<Path> folders = Files.walk(rootFolder)
                    .filter(Files::isDirectory)
                    .filter(p -> !p.equals(rootFolder)).collect(Collectors.toList());
            for (Path folder : folders) {
                System.out.println("Working with folder");
                try {
                    List<Path> files = Files.walk(folder)
                            .filter(Files::isRegularFile)
                            .filter(this::isNotHidden)
                            .collect(Collectors.toList());
                    for (Path file : files) {
                        uploadFile(mode, file.toFile());
                        System.out.println("Last File upload");
                    }
                    createRecord(mode);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            doCheck(idList);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Create Files is DONE");
    }

    private void doCheck(List<String> idList) {
        LinkedHashMap<?, ?> result_localized;
        System.out.println("Do check with " + idList);
        if (idList.isEmpty())
            return;
        else {
            String firstID = idList.get(0);
            System.out.println("getRecord: List is NOT Empty");
            for (String id : idList) {
                Response response = RestAssured.given()
                        .multiPart("token", token)
                        .get("https://test-box-webshow.cmai.tech/api/v2/records/" + firstID +
                                "?token=" + token);
                String status = response.getBody().path("status").toString();
                System.out.println("Status is " + status);
                if (status.equals("2")) {
                    System.out.println("Sleep 20 sec");
                    try {
                        Thread.sleep(20000);
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
                    idList.remove(firstID);
                    ExcelTable.fillTable(study);
                } else {
                    study.setId(response.body().path("id").toString());
                    study.setStatus(response.body().path("status").toString());
                    study.setStatusText(response.body().path("status_text"));
                    idList.remove(id);
                    ExcelTable.fillTable(study);
                }
                doCheck(idList);
            }
        }
    }

    private boolean isNotHidden(Path path) {
        try {
            return !Files.isHidden(path);
        } catch (IOException e) {
            System.err.println(e);
            return true;
        }
    }

    private void uploadFile(String mode, File file) {
        System.out.println("Upload is Started for " + file.toPath().getFileName());
        System.out.println(session_key);
        if (session_key == null) {
            session_key = RestAssured.given()
                    .multiPart("file", file, "multipart/form-data")
                    .post("https://test-box-webshow.cmai.tech/api/v2/records/file?mode=" + mode + "&token=" + token)
                    .then().extract().response().path("_session_key").toString();
            System.out.println("First file upload - " + session_key);
        } else {
            RestAssured.given()
                    .multiPart("file", file, "multipart/form-data")
                    .multiPart("_session_key", session_key)
                    .post("https://test-box-webshow.cmai.tech/api/v2/records/file?mode=" + mode + "&token=" + token);
//            System.out.println(session_key);
            System.out.println("Upload is Done");
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
        session_key = null;
        System.out.println("Create Record is Done, ID = " + id);
        System.out.println(session_key);
    }
}