// BingoController.java

package com.example.bingo;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene; // Залишаємо імпорт, бо DetailScene створюється тут
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.*;

public class BingoController {

    @FXML
    private GridPane gridPane; // Панель для сітки Бінго

    // Змінні для управління сценами та вікном
    private Stage mainStage;
    private MainApp mainApp; // Посилання на MainApp для перемикання сцен
    private GameMode currentGameMode; // Поточний режим гри

    // Списки подій
    private List<String> allEventsForMode = new ArrayList<>();
    private final List<Event> roundEvents = new ArrayList<>();
    private List<String> customEvents;

    // Прапорець, що гра завершена перемогою
    private boolean gameWon = false;

    // --- Сеттери для MainApp та Stage ---
    public void setMainStage(Stage stage) {
        this.mainStage = stage;
    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    public void setCurrentGameMode(GameMode mode) {
        this.currentGameMode = mode;
        System.out.println("BingoController: currentGameMode set to: " + mode);
    }

    public void setCustomEvents(List<String> customEvents) {
        this.customEvents = customEvents;
    }

    @FXML
    public void initialize() {
        System.out.println("BingoController: initialize() called (currentGameMode might be null here).");
        // Основна логіка ініціалізації гри перенесена в setupGame().
    }

    public void setupGame() {
        System.out.println("BingoController: setupGame() called with mode: " + currentGameMode);
        loadEventsForCurrentMode();
        prepareNewRound();
        renderGrid();
    }

    private void loadEventsForCurrentMode() {
        allEventsForMode.clear();
        switch (currentGameMode) {
            case WHAT_SHOULD_HAPPEN:
                allEventsForMode.addAll(getInitialEvents());
                allEventsForMode.addAll(getFiftyFiftyEvents());
                break;
            case WHAT_SHOULD_NOT_HAPPEN:
                allEventsForMode.addAll(getInitialEvents());
                allEventsForMode.addAll(getFiftyFiftyEvents());
                allEventsForMode.addAll(getWhatWillNotHappenEvents());
                break;
            case CUSTOM_GAME:
                if (customEvents != null && customEvents.size() >= 24) { // Перевірка кількості подій для CUSTOM_GAME
                    allEventsForMode.addAll(customEvents);
                } else {
                    System.err.println("Помилка: Режим 'Своя гра' обрано, але customEvents порожній або недостатньо подій.");
                    if (mainApp != null) {
                        // Повертаємося до привітальної сцени, якщо недостатньо подій у своїй грі
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Помилка");
                        alert.setHeaderText(null);
                        alert.setContentText("Для 'Своєї гри' потрібно принаймні 24 події. Повернення до головного меню.");
                        alert.showAndWait();
                        mainApp.returnToWelcomeScene();
                    }
                    return; // Важливо вийти, щоб не намагатися заповнити сітку з порожнім списком
                }
                break;
            default:
                System.err.println("Невідомий режим гри: " + currentGameMode);
                if (mainApp != null) {
                    mainApp.returnToWelcomeScene();
                }
                return;
        }
        System.out.println("Завантажено " + allEventsForMode.size() + " подій для режиму " + currentGameMode);
    }

    private void prepareNewRound() {
        roundEvents.clear();
        Collections.shuffle(allEventsForMode);

        for (int i = 0; i < 24 && i < allEventsForMode.size(); i++) {
            roundEvents.add(new Event(allEventsForMode.get(i)));
        }
        gameWon = false;
        System.out.println("Новий раунд підготовлено. Подій для раунду: " + roundEvents.size());
    }

    public void renderGrid() { // Зробив public, щоб mainApp міг викликати
        try {
            if (gridPane == null) {
                System.err.println("renderGrid: Помилка - gridPane is null!");
                return;
            }
            gridPane.getChildren().clear();
            int gridSize = 5;
            int eventListIndex = 0;

            for (int row = 0; row < gridSize; row++) {
                for (int col = 0; col < gridSize; col++) {
                    Node cellNode;

                    if (row == 2 && col == 2) {
                        Label freeLabel = new Label("★\nFREE");
                        freeLabel.setStyle("-fx-font-weight: bold; -fx-alignment: center; -fx-text-alignment: center; -fx-background-color: lightgrey;");
                        freeLabel.setPrefSize(100, 60);
                        freeLabel.setAlignment(Pos.CENTER);
                        cellNode = freeLabel;
                    } else {
                        if (eventListIndex >= roundEvents.size()) {
                            System.err.println("renderGrid: Помилка - не вистачає подій для клітинки (" + row + "," + col + "). Потрібно: 24, Доступно: " + roundEvents.size());
                            Region placeholder = new Region();
                            placeholder.setPrefSize(100, 60);
                            cellNode = placeholder;
                        } else {
                            Event currentEvent = roundEvents.get(eventListIndex);
                            Button btn = new Button(currentEvent.getDescription());
                            btn.setPrefSize(100, 60);
                            btn.setWrapText(true);
                            btn.setStyle("-fx-text-alignment: center;");

                            updateButtonStyle(btn, currentEvent.isMarked());

                            final int currentEventIndex = eventListIndex;
                            btn.setOnAction(e -> showEventDetails(currentEventIndex));
                            cellNode = btn;
                            eventListIndex++;
                        }
                    }
                    gridPane.add(cellNode, col, row);
                }
            }
        } catch (Exception e) {
            System.err.println("renderGrid: !!! ПОЙМАНА ПОМИЛКА !!!");
            e.printStackTrace();
        }
    }

    private void updateButtonStyle(Button btn, boolean marked) {
        if (marked) {
            btn.setStyle("-fx-base: lightgreen; -fx-text-alignment: center;");
        } else {
            btn.setStyle("-fx-text-alignment: center;");
        }
    }

    private void showEventDetails(int eventIndex) {
        if (eventIndex < 0 || eventIndex >= roundEvents.size()) {
            System.err.println("showEventDetails: Неправильний індекс події: " + eventIndex);
            return;
        }

        if (mainStage == null) {
            System.err.println("Критична помилка: mainStage is null!"); return;
        }

        Event event = roundEvents.get(eventIndex);

        Label label = new Label(event.getDescription());
        label.setWrapText(true);
        label.setStyle("-fx-font-size: 16px; -fx-text-alignment: center;");
        label.setMaxWidth(Double.MAX_VALUE);

        Button backBtn = new Button("Назад");
        CheckBox checkBox = new CheckBox("Виконано");
        checkBox.setSelected(event.isMarked());

        checkBox.setOnAction(e -> {
            if (gameWon) {
                checkBox.setSelected(event.isMarked());
                return;
            }
            boolean isMarked = checkBox.isSelected();
            event.setMarked(isMarked);
            Node nodeOnGrid = getNodeByEventIndex(eventIndex);
            if (nodeOnGrid instanceof Button) {
                updateButtonStyle((Button) nodeOnGrid, isMarked);
            }
            checkVictory();
        });

        backBtn.setOnAction(e -> renderGridScene()); // Викликаємо метод повернення до сітки

        HBox controls = new HBox(20, backBtn, checkBox);
        controls.setAlignment(Pos.CENTER);

        BorderPane detailPane = new BorderPane();
        detailPane.setCenter(label);
        detailPane.setBottom(controls);
        BorderPane.setAlignment(label, Pos.CENTER);
        BorderPane.setMargin(controls, new Insets(20, 0, 20, 0));
        detailPane.setPadding(new Insets(20));

        Scene detailScene = new Scene(detailPane, mainStage.getScene().getWidth(), mainStage.getScene().getHeight());
        mainStage.setScene(detailScene); // Встановлюємо сцену деталей
    }

    private Node getNodeByEventIndex(int eventIndex) {
        int currentEventIdx = 0;
        for (Node node : gridPane.getChildren()) {
            Integer nodeRow = GridPane.getRowIndex(node);
            Integer nodeCol = GridPane.getColumnIndex(node);

            if (nodeRow == null || nodeCol == null) continue;

            if (nodeRow == 2 && nodeCol == 2) continue;

            if (node instanceof Button) {
                if (currentEventIdx == eventIndex) {
                    return node;
                }
                currentEventIdx++;
            }
        }
        return null;
    }

    /**
     * Повертає користувача до головної сцени з сіткою Бінго, делегуючи це MainApp.
     */
    private void renderGridScene() {
        if (mainApp != null) {
            mainApp.returnToMainGameScene(); // Просимо MainApp повернути основну сцену
        } else {
            System.err.println("Помилка: MainApp посилання відсутнє. Неможливо повернутися до сітки.");
        }
    }

    private void checkVictory() {
        if (gameWon) return;

        int gridSize = 5;
        boolean winFound = false;

        // Перевірка рядків
        for (int row = 0; row < gridSize && !winFound; row++) {
            boolean lineComplete = true;
            for (int col = 0; col < gridSize; col++) {
                if (!isCellMarked(row, col)) {
                    lineComplete = false; break;
                }
            }
            if (lineComplete) winFound = true;
        }

        // Перевірка стовпців
        for (int col = 0; col < gridSize && !winFound; col++) {
            boolean lineComplete = true;
            for (int row = 0; row < gridSize; row++) {
                if (!isCellMarked(row, col)) {
                    lineComplete = false; break;
                }
            }
            if (lineComplete) winFound = true;
        }

        // Перевірка головної діагоналі (\)
        if (!winFound) {
            boolean lineComplete = true;
            for (int i = 0; i < gridSize; i++) {
                if (!isCellMarked(i, i)) {
                    lineComplete = false; break;
                }
            }
            if (lineComplete) winFound = true;
        }

        // Перевірка побічної діагоналі (/)
        if (!winFound) {
            boolean lineComplete = true;
            for (int i = 0; i < gridSize; i++) {
                if (!isCellMarked(i, gridSize - 1 - i)) {
                    lineComplete = false; break;
                }
            }
            if (lineComplete) winFound = true;
        }

        if (winFound) {
            gameWon = true;
            System.out.println("--- Перемога! ---");

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Перемога!");
            alert.setHeaderText(null);
            alert.setContentText("🎉 Бінго! Ви зібрали лінію!");
            alert.showAndWait();
        }
    }

    private boolean isCellMarked(int row, int col) {
        if (row == 2 && col == 2) return true;

        int linearIndex = row * 5 + col;
        int eventIndex = linearIndex;
        if (linearIndex >= 12) {
            eventIndex--;
        }

        if (eventIndex < 0 || eventIndex >= roundEvents.size()) {
            System.err.println("isCellMarked: Розраховано неправильний eventIndex (" + eventIndex + ") для (" + row + "," + col + "). roundEvents size: " + roundEvents.size());
            return false;
        }
        return roundEvents.get(eventIndex).isMarked();
    }

    public void handleRestartButtonAction() {
        System.out.println("--- Перезапуск гри ---");
        prepareNewRound();
        renderGrid();
    }

    @FXML
    public void handleBackToMenuButtonAction() {
        if (mainApp != null) {
            mainApp.returnToWelcomeScene();
        } else {
            System.err.println("MainApp посилання відсутнє. Неможливо повернутися до меню.");
        }
    }

    private List<String> getInitialEvents() {
        return Arrays.asList(
                "Викладач заходить у аудиторію", "Хтось запізнюється", "Викладач відкриває презентацію",
                "Хтось питає, що було на минулій парі", "Викладач каже \"це важливо\"", "Хтось дивиться час на телефоні",
                "Викладач запитує, чи є питання.", "Викладач дивиться на когось в очікуванні відповіді.",
                "Викладач повторює щось двічі.", "Викладач пише на дошці",
                "Хтось просить повторити сказане,тому що не встиг записати", "Викладач жартує", "Викладач звертається до конкретного студента",
                "Викладач згадує щось з минулих тем", "Хтось відповідає невпевнено", "Викладач говорить \"це буде на екзамені\"",
                "Викладач робить паузу в очікуванні тиші,у відповідь на галас", "Хтось пише конспект", "Викладач говорить \"ми вже це проходили\"",
                "Хтось хитає головою, ніби розуміє", "Викладач проводить аналогію з життям", "Викладач каже, що часу залишилося мало",
                "Викладач каже, що матеріал є в підручнику", "Викладач просить когось відповідати"
        );
    }

    private List<String> getFiftyFiftyEvents() {
        return Arrays.asList(
                "Викладач приходить із запізненням",
                "Викладач випадково каже щось смішне",
                "В аудиторії вимикають світло/електрику",
                "Хтось запитує про екзамен",
                "Хтось піднімає руку для відповіді",
                "Викладач згадує історію зі студентського життя",
                "Викладач плутається у матеріалі",
                "Викладач забуває, що казав",
                "Хтось щось роняє",
                "Викладач питає, хто не зробив домашку. або навпаки?",
                "Викладач раптово переходить на іншу тему",
                "Викладач задає риторичне питання",
                "Викладач каже \"на цьому ми закінчимо раніше\"",
                "Викладач каже \"це буде легке питання на екзамені\"",
                "Хтось сміється, але не через викладача",
                "Викладач забуває слово і шукає його",
                "Викладач випадково каже щось іншою мовою",
                "Викладач ставить несподіване запитання",
                "Хтось виходить із заняття раніше",
                "Викладач змінює тему посеред речення",
                "Викладач говорить фразу \"якби у нас було більше часу...\"",
                "Хтось голосно кашляє або чхає",
                "Викладач робить довгу паузу",
                "Хтось ставить дуже несподіване або дивне питання"
        );
    }

    private List<String> getWhatWillNotHappenEvents() {
        return Arrays.asList(
                "Викладач почне викладати абсолютно інший предмет, ніж той, що в розкладі, і не помітить цього",
                "Усі студенти в аудиторії принесуть абсолютно однакові напої та снеки",
                "Хтось із студентів випадково ввімкне пряму трансляцію невідомого відео на проекторі замість презентації",
                "Під час пари в аудиторію залетить екзотичний птах, і викладач продовжить лекцію, не звертаючи уваги",
                "Викладач почне використовувати слайди з іншої лекції, які не мають жодного відношення до поточної теми",
                "Усі студенти одночасно отримають однакове повідомлення від невідомого відправника, яке не має сенсу",
                "Хтось із студентів випадково переплутає свої конспекти з чужими, і викладач не помітить підміни",
                "Викладач почне розповідати про свій сон, який не має жодного відношення до предмету",
                "Усі студенти прийдуть на пару з однаковими виразами обличчя, ніби за командою",
                "Під час пари почнеться плановий ремонт у сусідній аудиторії з дуже гучним шумом, і пару не скасують",
                "Викладач оголосить, що сьогоднішня лекція буде присвячена розбору гороскопів студентів",
                "Хтось із студентів почне робити дуже складну аерографію на парті, і викладач не зупинить його",
                "На проекторі випадково відкриється особиста переписка викладача",
                "Усі студенти одночасно почнуть відчувати свербіж в одному й тому ж місці",
                "Викладач почне використовувати неіснуючі терміни та поняття, видаючи їх за загальновідомі",
                "Хтось принесе на пару дуже велику кількість їжі і почне демонстративно її їсти, не пропонуючи іншим",
                "Усі студенти раптово почнуть говорити цитатами з одного й того ж фільму",
                "Викладач почне оцінювати відповіді студентів кидком монетки",
                "Хтось із студентів випадково принесе на пару одяг викладача замість свого",
                "Під час пари в аудиторію випадково зайде прибиральник і почне мити підлогу, не звертаючи уваги на лекцію",
                "Викладач оголосить, що сьогоднішня пара буде присвячена обговоренню останніх новин шоу-бізнесу",
                "Усі студенти одночасно почнуть писати одне й те саме слово на полях своїх зошитів",
                "Хтось принесе на пару настільну гру і почне грати сам із собою",
                "Після закінчення пари виявиться, що всі годинники в аудиторії показують абсолютно різний час"
        );
    }
}