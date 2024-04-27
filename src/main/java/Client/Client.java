package application;

import java.io.*;
import java.net.Socket;

public class Client implements Runnable {
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private ChatUI ui;

    // 생성자 추가
    public Client(String serverAddress, int serverPort, ChatUI ui) throws IOException {
        this.ui = ui;
        socket = new Socket(serverAddress, serverPort);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new PrintWriter(socket.getOutputStream(), true);
    }

    public void sendMessage(String message) {
        writer.println(message);
    }

    @Override
    public void run() {
        try {
            String message;
            while ((message = reader.readLine()) != null) {
                ui.updateMessages(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
            javafx.application.Platform.runLater(() -> ui.updateMessages("Error: " + e.getMessage()));
        }
    }
}