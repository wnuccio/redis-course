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

        server.write("key1", "value1");

        timer.start();
        String value = server.read("key1");
        timer.stop();

        assertEquals("value1", value);
        long elapsedSeconds = timer.elapsedSeconds();
        assertTrue(elapsedSeconds > 2, "Latency: "+ elapsedSeconds);
    }

    @Test
    void server_returns_different_values_for_different_keys() {
        Server server = new Server(0);

        server.write("key1", "value1");
        server.write("key2", "value2");

        String value1 = server.read("key1");
        String value2 = server.read("key2");

        assertEquals("value1", value1);
        assertEquals("value2", value2);
    }


}
