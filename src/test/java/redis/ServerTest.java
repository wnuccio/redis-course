package redis;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ServerTest {

    @Test
    void server_returns_value_slowly() {
        Server server = new Server(3);
        Timer timer = new Timer();

        timer.start();
        String value = server.read("key");
        timer.stop();

        assertEquals("value", value);
        long elapsedSeconds = timer.elapsedSeconds();
        assertTrue(elapsedSeconds > 2, "Latency: "+ elapsedSeconds);
    }
}
