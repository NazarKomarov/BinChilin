import java.util.*;

public class Main {
    // Класс события
    static class Event {
        String description;
        boolean occurred;

        Event(String description) {
            this.description = description;
            this.occurred = false;
        }

        void markOccurred() {
            occurred = true;
        }
    }

    // Класс игрока
    static class Player {
        String name;
        int wins;

        Player(String name) {
            this.name = name;
            this.wins = 0;
        }

        void addWin() {
            wins++;
        }
    }

    // Таблица лидеров
    static class Leaderboard {
        Map<String, Player> players = new HashMap<>();

        void recordWin(String name) {
            players.putIfAbsent(name, new Player(name));
            players.get(name).addWin();
        }

        void show() {
            System.out.println("\n🏆 Таблиця лідерів:");
            players.values().stream()
                    .sorted((a, b) -> Integer.compare(b.wins, a.wins))
                    .forEach(p -> System.out.println(p.name + ": " + p.wins + " перемог"));
        }
    }

    // Главная логика игры
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        List<String> allEventDescriptions = getAllEvents();
        Leaderboard leaderboard = new Leaderboard();

        while (true) {
            System.out.print("\nВведіть ім'я учасника (або 'exit' для виходу): ");
            String playerName = scanner.nextLine();
            if (playerName.equalsIgnoreCase("exit")) break;

            // Генерация и перемешивание событий
            List<Event> roundEvents = new ArrayList<>();
            Collections.shuffle(allEventDescriptions);
            for (int i = 0; i < 24; i++) {
                roundEvents.add(new Event(allEventDescriptions.get(i)));
            }

            System.out.println("\n🔔 Івенти цього раунда:");
            for (int i = 0; i < roundEvents.size(); i++) {
                System.out.println((i + 1) + ". " + roundEvents.get(i).description);
            }

            // Пометка произошедших
            System.out.println("\nВедіть івент який стався (або 'stop' для зупинення гри):");
            while (true) {
                String input = scanner.nextLine();
                if (input.equalsIgnoreCase("stop")) break;
                try {
                    int index = Integer.parseInt(input) - 1;
                    if (index >= 0 && index < roundEvents.size()) {
                        roundEvents.get(index).markOccurred();
                        System.out.println("✅ Івент відмічено: " + roundEvents.get(index).description);
                    } else {
                        System.out.println("Невірний номер.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Введіть число.");
                }
            }

            // Проверка победы
            long occurredCount = roundEvents.stream().filter(e -> e.occurred).count();
            if (occurredCount >= 5) {
                System.out.println("🎉 Перемога! " + playerName + " Вигра(в/ла).");
                leaderboard.recordWin(playerName);
            } else {
                System.out.println("😕 Недостатньо івентів для перемоги. (" + occurredCount + " з 5)");
            }

            leaderboard.show();
        }

        System.out.println("Игра завершена.");
    }

    // Все 48 событий
    private static List<String> getAllEvents() {
        return Arrays.asList(
                "Викладач заходить у клас",
                "Хтось запізнюється",
                "Викладач відкриває презентацію",
                "Хтось питає, що було на минулій парі",
                "Викладач каже \"це важливо\"",
                "Хтось гортає соцмережі на телефоні",
                "Викладач запитує, чи є питання.",
                "Викладач дивиться на когось в очікуванні відповіді",
                "Викладач повторює щось двічі.",
                "Викладач пише на дошці",
                "Хтось просить повторити матеріал",
                "Викладач жартує",
                "Викладач звертається до конкретного студента",
                "Викладач згадує щось з минулих тем",
                "Хтось відповідає невпевнено",
                "Викладач говорить \"це буде на екзамені\"",
                "Викладач робить паузу в очікуванні тиші. у відповідь на галас?",
                "Хтось пише конспект",
                "Викладач говорить \"ми вже це проходили\"",
                "Хтось хитає головою, ніби розуміє",
                "Викладач проводить аналогію з життям",
                "Викладач каже, що часу залишилося мало",
                "Викладач каже, що матеріал є в підручнику",
                "Викладач просить когось відповідати",
                "Викладач приходить із запізненням",
                "Викладач випадково каже щось смішне",
                "В аудиторії вимикають світло/електрику",
                "Хтось запитує про екзамен",
                "Хтось піднімає руку для відповіді",
                "Викладач згадує історію зі студентського життя",
                "Викладач плутається у матеріалі.",
                "Викладач забуває, що казав",
                "Хтось щось роняє",
                "Викладач питає, хто не зробив домашку або навпаки",
                ".Викладач раптово переходить на іншу тему",
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
}
