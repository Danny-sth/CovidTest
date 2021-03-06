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
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;

public class Request {
    static List<Study> studies = new LinkedList<>();
    private static Set<String> idList = new CopyOnWriteArraySet<>();
    private static String token;
    private static String session_key = null;
    private static int folderCounter = 1;
    private static Response response;
    private static String status;

    public void loginOrRefresh(String URL,
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
        System.out.println("Refresh Token");
    }

    public void createFiles(String mode) {
        Path rootFolder = Path.of(Env.ROOT_FOLDER);
        try {
            List<Path> folders = Files.walk(rootFolder)
                    .filter(Files::isDirectory)
                    .filter(p -> !p.equals(rootFolder)).collect(Collectors.toList());
            for (Path folder : folders) {
                System.out.println("Working with folder number - " + folderCounter);
                loginOrRefresh(
                        Env.loginEndpoint,
                        Env.LOGIN,
                        Env.PASSWORD);
                try {
                    List<Path> files = Files.walk(folder)
                            .filter(Files::isRegularFile)
                            .filter(this::isNotHidden)
                            .collect(Collectors.toList());
                    for (Path file : files) {
                        uploadFile(mode, file.toFile());
                    }
                    System.out.println("Last file in folder is upload");
                    createRecord(mode);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                folderCounter += 1;
            }
            System.out.println("All files is upload");
            doCheck(idList);
        } catch (IOException e) {
            e.printStackTrace();
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
        try {
            if (session_key == null) {
                session_key = RestAssured.given()
                        .multiPart("file", file, "multipart/form-data")
                        .post(Env.createFilesEndpoint + "?mode=" + mode + "&token=" + token)
                        .then().extract().response().path("_session_key").toString();
            } else {
                RestAssured.given()
                        .multiPart("file", file, "multipart/form-data")
                        .multiPart("_session_key", session_key)
                        .post(Env.createFilesEndpoint + "?mode=" + mode + "&token=" + token);
            }
        } catch (Exception e) {
            e.printStackTrace();
            uploadFile(mode, file);
        }
    }

    private void createRecord(String mode) {
        String id = RestAssured.given().multiPart("mode", mode)
                .multiPart("token", token)
                .multiPart("_session_key", session_key)
                .post(Env.createRecordEndpoint + "?mode=" + mode +
                        "&token=" + token + "&_session_key=" + session_key)
                .then().extract().body().path("id").toString();
        idList.add(id);
        session_key = null;
        System.out.println("Record ID = " + id);
    }

    private void doCheck(Set<String> idList) {
        byte counter = 1;
        if (idList.isEmpty())
            return;
        else {
            for (String id : idList) {
                response = getRequest(id);
                status = response.getBody().path("status").toString();
                System.out.println("Status is " + status);
                while (status.equals("2") && counter != 13) {
                    System.out.println("Sleep 30 sec, number " + counter);
                    try {
                        Thread.sleep(30000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    response = getRequest(id);
                    status = response.getBody()
                            .path("status").toString();
                    counter += 1;
                }
                if (counter == 13) {
                    addMessageAboutTimeoutError(id);
                    counter = 1;
                } else {
                    addStudies(id);
                    counter = 1;
                }
            }
            ExcelTable.fillTable(studies);
        }
    }

    private Response getRequest(String id) {
        return RestAssured.given()
                .multiPart("token", token)
                .get(Env.getEndpoint + id +
                        "?token=" + token);
    }

    public void addStudies(String id) {
        if (status.equals("10")) {
            LinkedHashMap<?, ?> result_localized =
                    response.body().path("result_localized");
            studies.add(new Study(
                    response.body().path("series_iuid").toString(),
                    response.body().path("id").toString(),
                    result_localized.get("is_healthy").toString(),
                    result_localized.get("prob").toString(),  // prob ?????????? ?????????? ??????????????????????????????, ?????? ?????????????? ???? ????????????
                    response.body().path("status").toString(),
                    response.body().path("status_text")));
            idList.remove(id);
            System.out.println("1 study was added, studies - " + studies);
        } else {
            studies.add(new Study(
                    null,
                    response.body().path("id").toString(),
                    null, null,
                    response.body().path("status").toString(),
                    response.body().path("status_text")));
            idList.remove(id);
            System.out.println("1 study was added, studies - " + studies);
        }
    }

    private void addMessageAboutTimeoutError(String id) {
        System.out.println("Adding Message about Timeout Error");
        studies.add(new Study(
                response.body().path("series_iuid").toString(),
                response.body().path("id").toString(),
                null, null, null, "?????????????????? ?????????? ????????????????"));
        idList.remove(id);
    }
}