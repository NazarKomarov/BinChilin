package com.example.bingo;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.*;

public class BingoController {

    @FXML
    private GridPane gridPane;

    // Поле для зберігання основної сцени
    private Scene mainScene;

    // Поле для зберігання головного вікна (Stage)
    private Stage mainStage;

    private final List<String> allEvents = getAllEvents();
    private final List<Event> roundEvents = new ArrayList<>();
    private int score = 0;
    private final int WINNING_SCORE = 12;

    @FXML
    public void initialize() {
        prepareNewRound();
        renderGrid();
    }

    private void prepareNewRound() {
        score = 0;
        roundEvents.clear();
        Collections.shuffle(allEvents);
        for (int i = 0; i < 24 && i < allEvents.size(); i++) {
            roundEvents.add(new Event(allEvents.get(i)));
        }
    }

    private void renderGrid() {
        try {
            if (gridPane == null) {
                System.err.println("--- renderGrid: Помилка - gridPane is null! ---");
                return;
            }
            gridPane.getChildren().clear();
            int cols = 8;

            for (int i = 0; i < roundEvents.size(); i++) {
                int row = i / cols;
                int col = i % cols;
                Event currentEvent = roundEvents.get(i);
                if (currentEvent == null) {
                    System.err.println("--- renderGrid: Помилка - Event null в індексі " + i + " ---");
                    continue;
                }

                Button btn = new Button(String.valueOf(i + 1));
                btn.setPrefSize(100, 60);
                if (currentEvent.getDescription() != null) {
                    btn.setTooltip(new Tooltip(currentEvent.getDescription()));
                }

                if (currentEvent.isMarked()) {
                    btn.setStyle("-fx-base: lightgreen;");
                } else {
                    btn.setStyle("");
                }

                int eventIndex = i;
                btn.setOnAction(e -> showEventDetails(eventIndex));
                gridPane.add(btn, col, row);
            }
        } catch (Exception e) {
            System.err.println("--- renderGrid: !!! ПОЙМАНА ОШИБКА ВНУТРИ renderGrid !!! ---");
            e.printStackTrace();
        }
    }

    // Метод показу деталей івенту
    private void showEventDetails(int index) {
        // Зберігаємо основну сцену перед переключенням
        if (mainScene == null) {
            mainScene = gridPane.getScene();
            if (mainScene == null) {
                System.err.println("Критична помилка: Не вдалося отримати поточну сцену!");
                return;
            }
        }

        // Зберігаємо Stage, якщо еще не зробили цього
        if (mainStage == null) {
            mainStage = (Stage) mainScene.getWindow(); // Отримаємо з поточної сцени
            if (mainStage == null) {
                System.err.println("Критична помилка: Не вдалося отримати Stage!");
                return;
            }
        }

        Event event = roundEvents.get(index);

        Label label = new Label(event.getDescription());
        label.setWrapText(true);
        label.setStyle("-fx-font-size: 16px; -fx-text-alignment: center;");
        label.setMaxWidth(Double.MAX_VALUE);

        Button backBtn = new Button("Назад");
        CheckBox checkBox = new CheckBox("Виконано");
        checkBox.setSelected(event.isMarked());

        checkBox.setOnAction(e -> {
            boolean wasMarked = event.isMarked();
            boolean isMarked = checkBox.isSelected();
            event.setMarked(isMarked);
            if (isMarked && !wasMarked) score++;
            else if (!isMarked && wasMarked) score--;
            checkVictory();
        });

        backBtn.setOnAction(e -> renderGridScene());

        HBox controls = new HBox(20, backBtn, checkBox);
        controls.setAlignment(Pos.CENTER);

        BorderPane detailPane = new BorderPane();
        detailPane.setCenter(label);
        detailPane.setBottom(controls);
        BorderPane.setAlignment(label, Pos.CENTER);
        BorderPane.setMargin(controls, new Insets(20, 0, 20, 0));
        detailPane.setPadding(new Insets(20));

        // Створюємо нову сцену для деталей, використовуючи розміри вікна з збереженного Stage
        Scene detailScene = new Scene(detailPane, mainStage.getWidth(), mainStage.getHeight());

        // Встановлюємо новую сцену (деталі) на збережений Stage
        mainStage.setScene(detailScene);
    }

    // Метод для повернення до основнох сцени з сіткою
    private void renderGridScene() {
        // Використовуємо збережені mainStage и mainScene
        if (mainStage != null && mainScene != null) {
            renderGrid(); // Перемальоваємо сітку
            mainStage.setScene(mainScene); // Встановлюємо основну сцену на Stage
        } else {
            System.err.println("Помилка: Не Вдалося повернутися (mainStage или mainScene is null).");
            if(mainStage == null) System.err.println("Причина: mainStage == null");
            if(mainScene == null) System.err.println("Причина: mainScene == null");
        }
    }

    // Перевірка умови перемоги
    private void checkVictory() {
        if (score >= WINNING_SCORE) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Перемога!");
            alert.setHeaderText(null);
            alert.setContentText("🎉 Ви виграли раунд! Набрано поінтів: " + score);
            alert.showAndWait();
            prepareNewRound();
            renderGridScene();
        }
    }

    // Список всіх можливих івентів
    private List<String> getAllEvents() {
        // ... (список івентів) ...
        return Arrays.asList(
                "Викладач заходить у аудиторію", "Хтось запізнюється ", "Викладач відкриває презентацію",
                "Хтось питає, що було на минулій парі", "Викладач каже \"це важливо\"", "Хтось дивиться час на телефоні",
                "Викладач запитує, чи є питання.", "Викладач дивиться на когось в очікуванні відповіді.",
                "Викладач повторює щось двічі.", "Викладач пише на дошці",
                "Хтось просить повторити сказане,тому що не встиг записати", "Викладач жартує", "Викладач звертається до конкретного студента",
                "Викладач згадує щось з минулих тем", "Хтось відповідає невпевнено", "Викладач говорить \"це буде на екзамені\"",
                "Викладач робить паузу в очікуванні тиші,у відповідь на галас", "Хтось пише конспект", "Викладач говорить \"ми вже це проходили\"",
                "Хтось хитає головою, ніби розуміє", "Викладач проводить аналогію з життям", "Викладач каже, що часу залишилося мало",
                "Викладач каже, що матеріал є в підручнику", "Викладач просить когось відповідати", "Викладач приходить із запізненням",
                "Викладач випадково каже щось смішне", "В аудиторії вимикають світло/електрику", "Хтось запитує про екзамен", "Хтось піднімає руку для відповіді",
                "Викладач згадує історію зі студентського життя", "Викладач плутається у матеріалі", "Викладач забуває, що казав",
                "Хтось щось роняє", "Викладач питає, хто не зробив домашку. або навпаки?", "Викладач раптово переходить на іншу тему",
                "Викладач задає риторичне питання", "Викладач каже \"на цьому ми закінчимо раніше\"", "Викладач каже \"це буде легке питання на екзамені",
                "Хтось сміється, але не через викладача", "Викладач забуває слово і шукає його", "Викладач випадково каже щось іншою мовою",
                "Викладач ставить несподіване запитання", "Хтось виходить із заняття раніше", "Викладач змінює тему посеред речення",
                "Викладач говорить фразу \"якби у нас було більше часу...", "Хтось голосно кашляє або чхає", "Викладач робить довгу паузу",
                "Хтось ставить дуже несподіване або дивне питання"
        );
    }
}