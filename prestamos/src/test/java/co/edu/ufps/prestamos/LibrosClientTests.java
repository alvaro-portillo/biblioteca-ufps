package co.edu.ufps.prestamos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

class LibrosClientTests {

    @Test
    void traduceEl404DeLibrosA400() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/libros/99", exchange -> {
            exchange.sendResponseHeaders(404, -1);
            exchange.close();
        });
        server.start();

        try {
            LibrosClient client = new LibrosClient(
                    "http://127.0.0.1:" + server.getAddress().getPort());
            ResponseStatusException exception = assertThrows(
                    ResponseStatusException.class, () -> client.obtenerLibro(99L));
            assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        } finally {
            server.stop(0);
        }
    }

    @Test
    void traduceLaFaltaDeConexionA503() throws IOException {
        int puerto;
        try (ServerSocket socket = new ServerSocket(0, 1, InetAddress.getLoopbackAddress())) {
            puerto = socket.getLocalPort();
        }

        LibrosClient client = new LibrosClient("http://127.0.0.1:" + puerto);
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class, () -> client.obtenerLibro(1L));
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, exception.getStatusCode());
    }
}
