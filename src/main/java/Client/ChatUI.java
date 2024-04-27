package application;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

public class ChatUI extends Application {
    private Client client;
    private TextArea messageArea;
    private TextField inputField;
    private Button sendButton;
    private Button startGameButton;
    private TextField promptField;  // 제시어를 표시할 TextField
    private VBox rightPane;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("라이어게임");

        BorderPane root = new BorderPane();

        // 메시지 영역
        messageArea = new TextArea();
        messageArea.setEditable(false);
        root.setCenter(messageArea);

        // 입력 필드와 전송 버튼
        inputField = new TextField();
        sendButton = new Button("보내기");
        sendButton.setOnAction(e -> sendMessage());
        HBox inputPane = new HBox(inputField, sendButton);
        inputPane.setSpacing(10);
        inputPane.setPadding(new Insets(10));
        inputPane.setAlignment(Pos.CENTER);
        root.setBottom(inputPane);

        // 게임 시작 버튼 및 제시어 필드
        startGameButton = new Button("게임시작");
        startGameButton.setOnAction(e -> {
            client.sendMessage("start_game");
            startGameButton.setDisable(true);  // 게임 시작 요청 시 버튼 비활성화
        });
        promptField = new TextField();
        promptField.setEditable(false);  // 편집 불가능하도록 설정
        promptField.setMaxWidth(300);  // 제시어 필드 너비 설정
        promptField.setAlignment(Pos.CENTER);  // 텍스트 중앙 정렬

        // 우측 패널 설정
        rightPane = new VBox(10, startGameButton, promptField);
        rightPane.setPadding(new Insets(10));
        rightPane.setAlignment(Pos.TOP_CENTER);
        root.setRight(rightPane);

        Scene scene = new Scene(root, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.show();

        connectToServer();
    }

    private void connectToServer() {
        try {
            client = new Client("localhost", 1234, this);
            new Thread(client).start();
        } catch (Exception e) {
            updateMessages("서버연결 실패: " + e.getMessage());
        }
    }

    private void sendMessage() {
        String message = inputField.getText();
        if (!message.isEmpty()) {
            client.sendMessage(message);
            inputField.clear();
        }
    }

    public void setPrompt(String prompt) {
        javafx.application.Platform.runLater(() -> promptField.setText(prompt));
    }

    public void updateMessages(String message) {
        javafx.application.Platform.runLater(() -> {
            messageArea.appendText(message + "\n");
            if (message.equals("disable_start_button")) {
                startGameButton.setDisable(true);  // 서버로부터 비활성화 명령을 받으면 버튼 비활성화
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}