package application;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Pos;

public class ClientUI extends Application {
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Game Selection");

        // 이미지 파일로부터 BackgroundImage 생성
        Image backgroundImage = new Image("file:image/start.png");
        BackgroundImage background = new BackgroundImage(
            backgroundImage,
            BackgroundRepeat.NO_REPEAT,
            BackgroundRepeat.NO_REPEAT,
            BackgroundPosition.CENTER,
            new BackgroundSize(1.0, 1.0, true, true, false, true)
        );

        // 배경 이미지를 가진 BorderPane 생성
        BorderPane root = new BorderPane();
        root.setBackground(new Background(background));

        // 버튼 생성 및 추가
        Button liarGameButton = new Button("라이어 게임");
        liarGameButton.setOnAction(e -> showChatUI());

        Button twentyQuestionsButton = new Button("스무고개 게임");
        twentyQuestionsButton.setOnAction(e -> startTwentyQuestionsGame());

        Button multiplicationTableButton = new Button("구구단 게임");
        multiplicationTableButton.setOnAction(e -> startMultiplicationTableGame());

        // 버튼을 중앙 하단에 배치하기 위한 VBox 생성
        VBox gameButtons = new VBox(10); // 버튼 사이의 간격을 10으로 설정
        gameButtons.getChildren().addAll(liarGameButton, twentyQuestionsButton, multiplicationTableButton);
        gameButtons.setAlignment(Pos.CENTER); // VBox 내부의 버튼들을 가운데 정렬

        // BorderPane의 하단 중앙에 버튼 배치
        root.setBottom(gameButtons);
        BorderPane.setAlignment(gameButtons, Pos.CENTER);

        Scene scene = new Scene(root, 600, 400); // 적절한 크기로 설정
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private void showChatUI() {
        try {
            ChatUI chatUI = new ChatUI();
            Stage stage = new Stage();
            chatUI.start(stage);  // start 메소드가 Exception을 던질 수 있으므로, try-catch 블록 내에서 호출합니다.
            primaryStage.close(); // 현재 창을 닫습니다.
        } catch (Exception e) {
            e.printStackTrace(); // 예외 발생시 콘솔에 스택 트레이스를 출력
            // 필요하다면 사용자에게 오류 메시지를 보여주는 다이얼로그를 표시할 수 있습니다.
        }
    }

    private void startTwentyQuestionsGame() {
        // 스무고개 게임 시작 로직 구현
    }

    private void startMultiplicationTableGame() {
        // 구구단 게임 시작 로직 구현
    }
}