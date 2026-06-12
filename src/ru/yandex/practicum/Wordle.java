package ru.yandex.practicum;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;

public class Wordle {

    private static final String DICTIONARY_FILE = "words_ru.txt";
    private static final String LOG_FILE = "wordle.log";
    private static final String RESULT_URL =
            "http://localhost:8080/result";

    private static final String TOP_URL =
            "http://localhost:8080/top";

    public static void main(String[] args) {
        try (
                PrintWriter log =
                        new PrintWriter(new FileWriter(LOG_FILE, true));
                Scanner scanner = new Scanner(System.in)
        ) {

            WordleDictionaryLoader loader =
                    new WordleDictionaryLoader(log);

            WordleDictionary dictionary =
                    loader.load(DICTIONARY_FILE);

            WordleGame game =
                    new WordleGame(dictionary);

            System.out.println("Игра Wordle началась!");
            System.out.println("Угадайте слово из 5 букв.");
            System.out.println("Для получения подсказки введите пустую строку.");

            while (!game.isWon() && !game.isLost()) {

                System.out.println();
                System.out.println(
                        "Осталось попыток: " + game.getSteps()
                );

                System.out.print("Введите слово: ");

                String input = scanner.nextLine();

                if (input.isBlank()) {

                    String suggestion = game.getSuggestion();

                    if (suggestion == null) {
                        System.out.println("Подходящих слов не найдено.");
                    } else {
                        System.out.println(
                                "Подсказка компьютера: " + suggestion
                        );
                    }

                    continue;
                }

                try {
                    String hint = game.makeMove(input);
                    System.out.println(hint);

                } catch (InvalidWordLengthException |
                         InvalidWordException |
                         WordNotFoundInDictionaryException e) {

                    System.out.println(e.getMessage());
                }
            }

            System.out.println();

            if (game.isWon()) {

                System.out.println("Победа!");

                System.out.print("Введите никнейм: ");

                String nickname = scanner.nextLine().trim();

                if (nickname.isBlank()) {
                    nickname = "anonymous";
                }

                sendStatistic(
                        nickname,
                        game
                );

                showTopPlayers(nickname);

            } else {

                System.out.println("Поражение!");
            }

            if (!game.isWon()) {
                System.out.println(
                        "Загаданное слово: " + game.getAnswer()
                );
            }

        } catch (IOException e) {
            System.err.println(
                    "Ошибка загрузки словаря: "
                            + e.getMessage()
            );
        } catch (Exception e) {
            System.err.println(
                    "Непредвиденная ошибка: "
                            + e.getMessage()
            );
        }
    }

    private static void sendStatistic(
            String nickname,
            WordleGame game
    ) {

        try {

            PlayerStatistic statistic =
                    new PlayerStatistic(
                            nickname,
                            1,
                            6 - game.getSteps(),
                            !game.getUsedSuggestions().isEmpty()
                    );

            Gson gson = new Gson();

            URL url =
                    new URL(RESULT_URL);

            HttpURLConnection connection =
                    (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            connection.setRequestProperty(
                    "Content-Type",
                    "application/json"
            );

            String json =
                    gson.toJson(statistic);

            try (OutputStream os =
                         connection.getOutputStream()) {

                os.write(
                        json.getBytes(
                                StandardCharsets.UTF_8
                        )
                );
            }

            connection.getResponseCode();

        } catch (Exception e) {

            System.out.println(
                    "Не удалось отправить статистику."
            );
        }
    }

    private static void showTopPlayers(
            String nickname
    ) {

        try {

            URL url =
                    new URL(
                            TOP_URL
                                    + "?nickname="
                                    + nickname
                    );

            HttpURLConnection connection =
                    (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");

            Gson gson = new Gson();

            try (InputStreamReader reader =
                         new InputStreamReader(
                                 connection.getInputStream(),
                                 StandardCharsets.UTF_8
                         )) {

                Type listType =
                        new TypeToken<List<PlayerStatistic>>() {
                        }.getType();

                List<PlayerStatistic> top =
                        gson.fromJson(
                                reader,
                                listType
                        );

                System.out.println();
                System.out.println("Топ игроков:");

                for (PlayerStatistic player : top) {

                    System.out.println(
                            player.getPlace()
                                    + ". "
                                    + player.getNickname()
                                    + " - "
                                    + player.getWins()
                                    + " побед, "
                                    + player.getSteps()
                                    + " шагов"
                    );
                }
            }

        } catch (Exception e) {

            System.out.println(
                    "Не удалось получить рейтинг."
            );
        }
    }
}