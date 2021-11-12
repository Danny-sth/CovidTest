import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class Request {
    private static final List<String> idList = new CopyOnWriteArrayList<>();
    static List<Study> studies = new LinkedList<>();
    private static String token;
    private static String session_key = null;

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
        Path rootFolder = Path.of(Environment.rootFolder);
        try {
            // FIXME try to remove rootFolder filter
            List<Path> folders = Files.walk(rootFolder)
                    .filter(Files::isDirectory)
                    .filter(p -> !p.equals(rootFolder)).collect(Collectors.toList());
            for (Path folder : folders) {
                System.out.println("Working with next folder");
                refreshToken();
                System.out.println("Token is updated");
                try {
                    List<Path> files = Files.walk(folder)
                            .filter(Files::isRegularFile)
                            .filter(this::isNotHidden)
                            .collect(Collectors.toList());
                    for (Path file : files) {
                        uploadFile(mode, file.toFile());
                    }
                    System.out.println("Last File in folder is upload");
                    createRecord(mode);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("Create Files is DONE");
            doCheck(idList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void refreshToken() {
        login(Environment.loginEndpoint,
                Environment.LOGIN,
                Environment.PASSWORD);
    }

    private void doCheck(List<String> idList) {
        LinkedHashMap<?, ?> result_localized;
        System.out.println("Do check with " + idList);
        if (idList.isEmpty())
            return;
        else {
            System.out.println("getRecord: List is NOT Empty");
            for (String id : idList) {
                Response response = RestAssured.given()
                        .multiPart("token", token)
                        .get(Environment.getEndpoint + id +
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
                    System.out.println("Status Analyzed (10)");
                    result_localized = response.body().path("result_localized");
                    studies.add(new Study(
                            response.body().path("id").toString(),
                            result_localized.get("is_healthy").toString(),
                            result_localized.get("prob").toString(),  // prob после теста закоментировать, его вырежут из ответа
                            response.body().path("status").toString(),
                            response.body().path("status_text")));
                    idList.remove(id);
                    System.out.println("1 study was added, studies - " + studies);
                } else {
                    studies.add(new Study(
                            response.body().path("id").toString(),
                            null, null,
                            response.body().path("status").toString(),
                            response.body().path("status_text")));
                    idList.remove(id);
                    System.out.println("1 study was added, studies - " + studies);
                }
            }
        }
        ExcelTable.fillTable(studies);
        doCheck(idList);
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
        try {
            if (session_key == null) {
                session_key = RestAssured.given()
                        .multiPart("file", file, "multipart/form-data")
                        .post(Environment.createFilesEndpoint + "?mode=" + mode + "&token=" + token)
                        .then().extract().response().path("_session_key").toString();
                System.out.println("First file upload - " + session_key);
            } else {
                RestAssured.given()
                        .multiPart("file", file, "multipart/form-data")
                        .multiPart("_session_key", session_key)
                        .post(Environment.createFilesEndpoint + "?mode=" + mode + "&token=" + token);
            }
        } catch (Exception e) {
            e.printStackTrace();
            uploadFile(mode, file);
        }
    }

    private void createRecord(String mode) {
        System.out.println("Create Record is Started");
        String id = RestAssured.given().multiPart("mode", mode)
                .multiPart("token", token)
                .multiPart("_session_key", session_key)
                .post(Environment.createRecordEndpoint + "?mode=" + mode +
                        "&token=" + token + "&_session_key=" + session_key)
                .then().extract().body().path("id").toString();
        idList.add(id);
        session_key = null;
        System.out.println("Create Record is Done, ID = " + id);
        System.out.println("Now session_key - " + session_key);
    }
}