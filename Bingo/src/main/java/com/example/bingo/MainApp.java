// MainApp.java

package com.example.bingo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import javafx.geometry.Insets;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainApp extends Application {

    private Stage primaryStage;
    private BingoController bingoController;
    private Scene mainGameScene; // Зберігаємо посилання на основну ігрову сцену
    private Scene welcomeScene;
    private Scene customGameInputScene;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        primaryStage.setTitle("Бінго Івентів");
        showWelcomeScene();
    }

    private void showWelcomeScene() {
        VBox welcomeLayout = new VBox(20);
        welcomeLayout.setAlignment(Pos.CENTER);
        welcomeLayout.setPadding(new Insets(50));

        Button mode1Button = new Button("Що повинно статися на парі");
        Button mode2Button = new Button("Що не повинно статися на парі");
        Button mode3Button = new Button("Своя гра");

        mode1Button.setOnAction(e -> startGame(GameMode.WHAT_SHOULD_HAPPEN));
        mode2Button.setOnAction(e -> startGame(GameMode.WHAT_SHOULD_NOT_HAPPEN));
        mode3Button.setOnAction(e -> showCustomGameInputScene());

        welcomeLayout.getChildren().addAll(mode1Button, mode2Button, mode3Button);

        welcomeScene = new Scene(welcomeLayout, 900, 450);
        primaryStage.setScene(welcomeScene);
        primaryStage.show();
    }

    private void startGame(GameMode mode) {
        try {
            URL fxmlLocation = getClass().getResource("/com/example/bingo/bingo-view.fxml");
            if (fxmlLocation == null) {
                System.err.println("Не могу найти FXML файл! Проверьте путь: /com/example/bingo/bingo-view.fxml");
                return;
            }
            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            Parent root = loader.load();

            bingoController = loader.getController();
            bingoController.setMainStage(primaryStage);
            bingoController.setMainApp(this);
            bingoController.setCurrentGameMode(mode);
            // Створюємо Scene тут і зберігаємо її, оскільки це основна сцена гри
            mainGameScene = new Scene(root, 900, 450);
            primaryStage.setScene(mainGameScene); // Встановлюємо сцену вперше
            bingoController.setupGame(); // <--- Виклик setupGame() після встановлення сцени

            primaryStage.show();

        } catch (IOException e) {
            System.err.println("Помилка завантаження FXML:");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Виникла помилка при запуску застосунку:");
            e.printStackTrace();
        }
    }

    private void showCustomGameInputScene() {
        VBox customInputLayout = new VBox(10);
        customInputLayout.setAlignment(Pos.CENTER);
        customInputLayout.setPadding(new Insets(20));

        Label instructions = new Label("Введіть 24 події для Вашої гри (кожна подія з нового рядка):");
        instructions.setWrapText(true);

        TextArea inputTextArea = new TextArea();
        inputTextArea.setPrefRowCount(15);
        inputTextArea.setPromptText("Введіть ваші події тут...");

        Button startGameButton = new Button("Почати Свою Гру");
        Button backButton = new Button("Назад до вибору режиму");

        startGameButton.setOnAction(e -> {
            String inputText = inputTextArea.getText();
            List<String> customEvents = parseCustomEvents(inputText);
            if (customEvents.size() < 24) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Недостатньо подій");
                alert.setHeaderText(null);
                alert.setContentText("Будь ласка, введіть принаймні 24 події.");
                alert.showAndWait();
            } else {
                startGame(GameMode.CUSTOM_GAME, customEvents);
            }
        });

        backButton.setOnAction(e -> showWelcomeScene());

        customInputLayout.getChildren().addAll(instructions, inputTextArea, startGameButton, backButton);

        customGameInputScene = new Scene(customInputLayout, 900, 450);
        primaryStage.setScene(customGameInputScene);
    }

    private void startGame(GameMode mode, List<String> customEvents) {
        try {
            URL fxmlLocation = getClass().getResource("/com/example/bingo/bingo-view.fxml");
            if (fxmlLocation == null) {
                System.err.println("Не могу найти FXML файл! Проверьте путь: /com/example/bingo/bingo-view.fxml");
                return;
            }
            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            Parent root = loader.load();

            bingoController = loader.getController();
            bingoController.setMainStage(primaryStage);
            bingoController.setMainApp(this);
            bingoController.setCurrentGameMode(mode);
            bingoController.setCustomEvents(customEvents);
            // Створюємо Scene тут і зберігаємо її, оскільки це основна сцена гри
            mainGameScene = new Scene(root, 900, 450);
            primaryStage.setScene(mainGameScene); // Встановлюємо сцену вперше
            bingoController.setupGame(); // <--- Виклик setupGame() після встановлення сцени

            primaryStage.show();

        } catch (IOException e) {
            System.err.println("Помилка завантаження FXML:");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Виникла помилка при запуску застосунку:");
            e.printStackTrace();
        }
    }

    private List<String> parseCustomEvents(String inputText) {
        List<String> events = new ArrayList<>();
        String[] lines = inputText.split("\\r?\\n");
        for (String line : lines) {
            String trimmedLine = line.trim();
            if (!trimmedLine.isEmpty()) {
                events.add(trimmedLine);
            }
        }
        return events;
    }

    public void returnToWelcomeScene() {
        showWelcomeScene();
    }

    /**
     * Новий метод для повернення до основної ігрової сцени.
     * Викликається з BingoController.
     */
    public void returnToMainGameScene() {
        if (primaryStage != null && mainGameScene != null) { // ВИПРАВЛЕНО ТУТ: mainStage -> primaryStage
            primaryStage.setScene(mainGameScene);
            // Оскільки ми повертаємося до існуючої сцени,
            // gridPane в ній вже має бути, але ми хочемо його оновити.
            // Можливо, потрібно викликати renderGrid() з контролера,
            // щоб оновити стан кнопок.
            if (bingoController != null) {
                bingoController.renderGrid(); // Викликаємо оновлення сітки
            }
            primaryStage.show();
        } else {
            System.err.println("Помилка: Не вдалося повернутися до основної ігрової сцени (primaryStage або mainGameScene is null)."); // Оновлено повідомлення
            // Якщо не можемо повернутися, можливо, варто повернутися до привітальної сцени як запасний варіант
            showWelcomeScene();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}