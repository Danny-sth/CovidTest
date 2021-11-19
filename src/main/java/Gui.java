import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class Gui extends JFrame {

    private final JButton START_BUTTON = new JButton("СТАРТ");
    private JTextField contour = new JTextField(Env.DOMEN);
    private JTextField mode = new JTextField(Env.MODE);
    private JTextField rootFolder = new JTextField(Env.ROOT_FOLDER);
    private JTextField fileName = new JTextField(Env.FILE_NAME);
    private JTextField login = new JTextField(Env.LOGIN);
    private JTextField password = new JTextField(Env.PASSWORD);

    public Gui() {
        super("CMAI Uploader");
        this.setBounds(0, 0, 700, 200);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Container container = this.getContentPane();
        container.setLayout(new GridLayout(4, 2, 2, 2));
        container.add(contour);
        container.add(mode);
        container.add(rootFolder);
        container.add(fileName);
        container.add(login);
        container.add(password);
        START_BUTTON.addActionListener((action) -> {
            Env.DOMEN = contour.getText();
            Env.MODE = mode.getText();
            Env.ROOT_FOLDER = rootFolder.getText();
            Env.FILE_NAME = fileName.getText();
            Env.LOGIN = login.getText();
            Env.PASSWORD = password.getText();
            new ConsoleToLog();
            new Request().createFiles(Env.MODE);
        });
        container.add(START_BUTTON);
    }

    public class ConsoleToLog {
        public ConsoleToLog() {
            LogWindow logWindow = new LogWindow();
            logWindow.start();
            System.setOut(new PrintStream(new RedirectingOutputStream(logWindow), true));
        }
    }

    class LogWindow extends JFrame {
        private JTextArea textArea = new JTextArea("123");

        public LogWindow() {
            super("Log");
            this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            Container container = this.getContentPane();
            container.add(textArea);
        }

        public void start() {
            this.setVisible(true);
        }

        public void appendText(String text) {
            textArea.append(text);
        }
    }

    public class RedirectingOutputStream extends OutputStream {

        private LogWindow logWindow;

        public RedirectingOutputStream(LogWindow logWindow) {
            this.logWindow = logWindow;
        }

        @Override
        public void write(int b) throws IOException {
            logWindow.appendText(String.valueOf((char) b));
        }
    }
}

