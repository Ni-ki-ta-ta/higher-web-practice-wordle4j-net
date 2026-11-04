package ru.yandex.practicum;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WordleServerStatisticLoaderTest {

    private WordleServerStatisticLoader loader;

    @BeforeEach
    void setUp() throws IOException {

        loader =
                new WordleServerStatisticLoader(
                        "test-statistics.json"
                );

        loader.save(List.of());
    }

    @Test
    void shouldSaveAndLoadStatistics()
            throws IOException {

        List<PlayerStatistic> statistics =
                List.of(
                        new PlayerStatistic(
                                "nikita",
                                3,
                                4,
                                false
                        )
                );

        loader.save(statistics);

        List<PlayerStatistic> loaded =
                loader.load();

        assertEquals(
                1,
                loaded.size(),
                "После сохранения должна загружаться одна запись"
        );

        assertEquals(
                "nikita",
                loaded.get(0).getNickname(),
                "Никнейм должен корректно сохраняться"
        );
    }

    @Test
    void shouldAddNewPlayer()
            throws IOException {

        loader.addResult(
                new PlayerStatistic(
                        "alex",
                        1,
                        3,
                        false
                )
        );

        List<PlayerStatistic> loaded =
                loader.load();

        assertEquals(
                1,
                loaded.size(),
                "Новый игрок должен добавляться в статистику"
        );
    }

    @Test
    void shouldIncreaseWinsForExistingPlayer()
            throws IOException {

        loader.save(
                List.of(
                        new PlayerStatistic(
                                "alex",
                                1,
                                5,
                                false
                        )
                )
        );

        loader.addResult(
                new PlayerStatistic(
                        "alex",
                        1,
                        4,
                        true
                )
        );

        List<PlayerStatistic> loaded =
                loader.load();

        assertEquals(
                2,
                loaded.get(0).getWins(),
                "Количество побед существующего игрока должно увеличиваться"
        );

        assertEquals(
                4,
                loaded.get(0).getSteps(),
                "Количество шагов должно обновляться последним результатом"
        );

        assertTrue(
                loaded.get(0).isUsedHints(),
                "Информация об использовании подсказок должна обновляться"
        );
    }

    @Test
    void shouldReturnTopPlayers()
            throws IOException {

        loader.save(
                List.of(
                        new PlayerStatistic(
                                "first",
                                10,
                                3,
                                false
                        ),
                        new PlayerStatistic(
                                "second",
                                5,
                                4,
                                false
                        )
                )
        );

        List<PlayerStatistic> top =
                loader.getTop10();

        assertEquals(
                "first",
                top.get(0).getNickname(),
                "Первым должен быть игрок с наибольшим количеством побед"
        );
    }

    @Test
    void shouldReturnOnlyTenPlayers()
            throws IOException {

        List<PlayerStatistic> statistics =
                new ArrayList<>();

        for (int i = 0; i < 15; i++) {

            statistics.add(
                    new PlayerStatistic(
                            "player" + i,
                            i,
                            3,
                            false
                    )
            );
        }

        loader.save(statistics);

        List<PlayerStatistic> top =
                loader.getTop10();

        assertEquals(
                10,
                top.size(),
                "Метод getTop10 должен возвращать не более десяти игроков"
        );
    }

    @Test
    void shouldAddPlayerOutsideTop10WithRealPlace() throws IOException {

        List<PlayerStatistic> statistics = new ArrayList<>();

        for (int i = 1; i <= 15; i++) {

            PlayerStatistic player =
                    new PlayerStatistic(
                            "player" + i,
                            16 - i,
                            3,
                            false
                    );

            statistics.add(player);
        }

        loader.save(statistics);

        List<PlayerStatistic> result =
                loader.getTop10WithPlayer("player15");

        assertEquals(
                11,
                result.size(),
                "Топ-10 должен содержать дополнительного игрока вне рейтинга"
        );

        PlayerStatistic lastPlayer =
                result.get(result.size() - 1);

        assertEquals(
                "player15",
                lastPlayer.getNickname(),
                "Последним должен быть запрошенный игрок"
        );

        assertEquals(
                15,
                lastPlayer.getPlace(),
                "Игрок должен видеть своё реальное место в общем рейтинге"
        );
    }

    @Test
    void shouldNotAddPlayerIfAlreadyInTop10() throws IOException {

        List<PlayerStatistic> statistics = new ArrayList<>();

        for (int i = 1; i <= 10; i++) {

            statistics.add(
                    new PlayerStatistic(
                            "player" + i,
                            11 - i,
                            3,
                            false
                    )
            );
        }

        loader.save(statistics);

        List<PlayerStatistic> result =
                loader.getTop10WithPlayer("player1");

        assertEquals(
                10,
                result.size(),
                "Игрок из топ-10 не должен дублироваться"
        );
    }
}