import io.restassured.RestAssured;

public class GetRecordThread extends Request implements Runnable {

    @Override
    public void run() {
        getRecord();
    }
}
