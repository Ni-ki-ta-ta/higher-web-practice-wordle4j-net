package ru.yandex.practicum;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class WordleServer {

    private static final int PORT = 8080;

    public static void main(String[] args) throws IOException {

        HttpServer server =
                HttpServer.create(
                        new InetSocketAddress(PORT),
                        0
                );

        WordleServerStatisticLoader loader =
                new WordleServerStatisticLoader();

        Gson gson = new Gson();

        server.createContext(
                "/result",
                exchange -> handleResult(
                        exchange,
                        loader,
                        gson
                )
        );

        server.createContext(
                "/top",
                exchange -> handleTop(
                        exchange,
                        loader,
                        gson
                )
        );

        server.setExecutor(null);
        server.start();

        System.out.println(
                "WordleServer запущен на порту "
                        + PORT
        );
    }

    private static void handleResult(
            HttpExchange exchange,
            WordleServerStatisticLoader loader,
            Gson gson
    ) throws IOException {

        if (!exchange.getRequestMethod()
                .equalsIgnoreCase("POST")) {

            exchange.sendResponseHeaders(
                    405,
                    -1
            );

            return;
        }

        try (InputStreamReader reader =
                     new InputStreamReader(
                             exchange.getRequestBody(),
                             StandardCharsets.UTF_8
                     )) {

            PlayerStatistic statistic =
                    gson.fromJson(
                            reader,
                            PlayerStatistic.class
                    );

            loader.addResult(statistic);

            String response =
                    "{\"status\":\"ok\"}";

            byte[] bytes =
                    response.getBytes(
                            StandardCharsets.UTF_8
                    );

            exchange.getResponseHeaders().add(
                    "Content-Type",
                    "application/json; charset=UTF-8"
            );

            exchange.sendResponseHeaders(
                    200,
                    bytes.length
            );

            try (OutputStream os =
                         exchange.getResponseBody()) {

                os.write(bytes);
            }
        }
    }

    private static void handleTop(
            HttpExchange exchange,
            WordleServerStatisticLoader loader,
            Gson gson
    ) throws IOException {

        if (!exchange.getRequestMethod()
                .equalsIgnoreCase("GET")) {

            exchange.sendResponseHeaders(
                    405,
                    -1
            );

            return;
        }

        String nickname = "";

        String query =
                exchange.getRequestURI()
                        .getQuery();

        if (query != null
                && query.startsWith("nickname=")) {

            nickname =
                    query.substring(
                            "nickname=".length()
                    );
        }

        List<PlayerStatistic> top =
                loader.getTop10WithPlayer(
                        nickname
                );

        String response =
                gson.toJson(top);

        byte[] bytes =
                response.getBytes(
                        StandardCharsets.UTF_8
                );

        exchange.getResponseHeaders().add(
                "Content-Type",
                "application/json; charset=UTF-8"
        );

        exchange.sendResponseHeaders(
                200,
                bytes.length
        );

        try (OutputStream os =
                     exchange.getResponseBody()) {

            os.write(bytes);
        }
    }
}
