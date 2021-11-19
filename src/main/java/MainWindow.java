import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class MainWindow extends JFrame {

    public static JTextArea textArea = new JTextArea( 25, 80);
    private final JButton START_BUTTON = new JButton("СТАРТ");
    private JTextField contour = new JTextField(Env.DOMEN);
    private JTextField mode = new JTextField(Env.MODE);
    private JTextField rootFolder = new JTextField(Env.ROOT_FOLDER);
    private JTextField fileName = new JTextField(Env.FILE_NAME);
    private JTextField login = new JTextField(Env.LOGIN);
    private JTextField password = new JTextField(Env.PASSWORD);

    public MainWindow() {
        super("CMAI Uploader");
        this.setBounds(0, 0, 900, 400);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Container container = this.getContentPane();
        container.setLayout(new GridLayout(4, 2, 2, 2));
        container.add(contour);
        container.add(mode);
        container.add(rootFolder);
        container.add(fileName);
        container.add(login);
        container.add(password);
        container.add(textArea);
        container.add(
                new JScrollPane(
                        textArea,
                        JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED),
                BorderLayout.CENTER);
        START_BUTTON.addActionListener((action) -> {
            Env.DOMEN = contour.getText();
            Env.MODE = mode.getText();
            Env.ROOT_FOLDER = rootFolder.getText();
            Env.FILE_NAME = fileName.getText();
            Env.LOGIN = login.getText();
            Env.PASSWORD = password.getText();
            JTextAreaOutputStream out =
                    new JTextAreaOutputStream(textArea);
            System.setOut(new PrintStream(out));
            new Thread(() -> {
                new Request()
                        .createFiles(Env.MODE);
            }).start();
        });
        container.add(START_BUTTON);
    }

    static class JTextAreaOutputStream extends OutputStream {
        private final JTextArea destination;

        public JTextAreaOutputStream(JTextArea destination) {
            if (destination == null)
                throw new IllegalArgumentException("Destination is null");

            this.destination = destination;
        }

        @Override
        public void write(byte[] buffer, int offset, int length) throws IOException {
            final String text = new String(buffer, offset, length);
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    destination.append(text);
                }
            });
        }

        @Override
        public void write(int b) throws IOException {
            write(new byte[]{(byte) b}, 0, 1);
        }
    }

}

