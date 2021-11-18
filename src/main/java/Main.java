import javax.sql.DataSource;

public class Main {

    public static void main(String[] args) {
        new Request()
                .createFiles(Env.MODE);
    }
}
