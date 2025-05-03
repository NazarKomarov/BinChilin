import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.stream.Collectors;

public class EventServer {
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/events", new EventHandler());
        server.setExecutor(null); // За замовчуванням
        server.start();
        System.out.println("Сервер запущено на http://localhost:8000");
    }

    static class EventHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            List<String> events = List.of(
                "Впав стакан", "Хтось чхнув", "Пролилась вода",
                "Зателефонував телефон", "Хтось вийшов", "Почався дощ"
            );

            String json = events.stream()
                .map(s -> "\"" + s + "\"")
                .collect(Collectors.joining(",", "[", "]"));

            // ⛑️ CORS і content-type: ОБОВ’ЯЗКОВО ДО sendResponseHeaders()
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");

            byte[] response = json.getBytes("UTF-8");
            exchange.sendResponseHeaders(200, response.length);

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response);
            }
        }
    }
}
