module com.example.bingo { // Имя модуля, обычно совпадает с базовым пакетом
    // Указываем, какие модули JavaFX нам нужны
    requires javafx.controls;
    requires javafx.fxml;

    // Открываем наш пакет для модуля javafx.fxml,
    // чтобы FXMLLoader мог получить доступ к контроллеру и его полям @FXML
    opens com.example.bingo to javafx.fxml;

    // Экспортируем наш пакет, чтобы главный класс мог быть запущен
    exports com.example.bingo;
}