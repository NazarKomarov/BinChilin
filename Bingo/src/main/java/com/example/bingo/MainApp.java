package com.example.bingo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Знаходимо та вигружаємо FXML файл
            // Важливо: Шлях до ресурсу вказується відносно classpath
            // Якщр FXML лежить в resources/com/example/bingo, путь буде /com/example/bingo/bingo-view.fxml
            URL fxmlLocation = getClass().getResource("/com/example/bingo/bingo-view.fxml");
            if (fxmlLocation == null) {
                 System.err.println("Не могу найти FXML файл! Проверьте путь: /com/example/bingo/bingo-view.fxml");
                 return;
            }
            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            Parent root = loader.load(); // Загружаємо корінний улемент з FXML

            // Отримуємо контроллер, щоб передати йому силку на сцену
            BingoController controller = loader.getController();

            // Создаємо основну сцену
            Scene scene = new Scene(root, 900, 450); // Використвовуємо розміри з FXML

            // Передаємосцену контролеру

            // Настройка головного вікна (Stage)
            primaryStage.setTitle("Бінго Івентів");
            primaryStage.setScene(scene);
            primaryStage.show(); // Показуємо вікно

        } catch (IOException e) {
            System.err.println("Помилка завантаження FXML:");
            e.printStackTrace();
        } catch (Exception e) {
             System.err.println("Виникла помилка при запуску застосунку:");
             e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args); // Запускаемо JavaFX застосунок
    }
}