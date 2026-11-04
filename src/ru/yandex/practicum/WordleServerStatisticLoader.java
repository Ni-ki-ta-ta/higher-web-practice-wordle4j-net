package ru.yandex.practicum;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class WordleServerStatisticLoader {

    private final Gson gson;

    private final String fileName;

    public WordleServerStatisticLoader() {
        this("statistics.json");
    }

    public WordleServerStatisticLoader(
            String fileName
    ) {
        this.fileName = fileName;

        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
    }

    public List<PlayerStatistic> load()
            throws IOException {

        Path path = Path.of(fileName);

        if (!Files.exists(path)) {
            return new ArrayList<>();
        }

        try (Reader reader =
                     Files.newBufferedReader(
                             path,
                             StandardCharsets.UTF_8
                     )) {

            Type listType =
                    new TypeToken<List<PlayerStatistic>>() {
                    }.getType();

            List<PlayerStatistic> statistics =
                    gson.fromJson(reader, listType);

            return statistics == null
                    ? new ArrayList<>()
                    : statistics;
        }
    }

    public void save(
            List<PlayerStatistic> statistics
    ) throws IOException {

        try (Writer writer =
                     Files.newBufferedWriter(
                             Path.of(fileName),
                             StandardCharsets.UTF_8
                     )) {

            gson.toJson(statistics, writer);
        }
    }

    public void addResult(
            PlayerStatistic result
    ) throws IOException {

        List<PlayerStatistic> statistics =
                load();

        boolean playerFound = false;

        for (PlayerStatistic statistic : statistics) {

            if (statistic.getNickname()
                    .equals(result.getNickname())) {

                statistic.incrementWins();

                statistic.setSteps(
                        result.getSteps()
                );

                statistic.setUsedHints(
                        result.isUsedHints()
                );

                playerFound = true;
                break;
            }
        }

        if (!playerFound) {

            result.setWins(1);

            statistics.add(result);
        }

        save(statistics);
    }

    public List<PlayerStatistic> getTop10()
            throws IOException {

        List<PlayerStatistic> statistics =
                load();

        statistics.sort(
                Comparator.comparingInt(
                        PlayerStatistic::getWins
                ).reversed()
        );

        if (statistics.size() > 10) {
            return statistics.subList(0, 10);
        }

        return statistics;
    }

    public List<PlayerStatistic> getTop10WithPlayer(
            String nickname
    ) throws IOException {

        List<PlayerStatistic> statistics =
                load();

        statistics.sort(
                Comparator.comparingInt(
                        PlayerStatistic::getWins
                ).reversed()
        );

        List<PlayerStatistic> result =
                new ArrayList<>();

        int limit =
                Math.min(10, statistics.size());

        for (int i = 0; i < limit; i++) {
            result.add(statistics.get(i));
        }

        boolean playerInTop =
                result.stream()
                        .anyMatch(player ->
                                player.getNickname()
                                        .equals(nickname)
                        );

        if (!playerInTop) {

            for (PlayerStatistic player : statistics) {

                if (player.getNickname()
                        .equals(nickname)) {

                    player.setPlace(
                            statistics.indexOf(player) + 1
                    );

                    result.add(player);
                    break;
                }
            }
        }

        for (int i = 0; i < result.size(); i++) {

            result.get(i).setPlace(i + 1);
        }

        if (!playerInTop
                && !result.isEmpty()) {

            PlayerStatistic lastPlayer =
                    result.get(result.size() - 1);

            lastPlayer.setPlace(
                    statistics.indexOf(lastPlayer) + 1
            );
        }

        return result;
    }
}
