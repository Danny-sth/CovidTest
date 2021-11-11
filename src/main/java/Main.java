public class Main {

    public static void main(String[] args) {
        Request request = new Request();
        request.login(Environment.loginEndpoint, Environment.LOGIN, Environment.PASSWORD);
        request.createFiles(Environment.MODE);
    }
}
