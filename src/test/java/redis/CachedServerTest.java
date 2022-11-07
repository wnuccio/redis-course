package redis;

import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CachedServerTest {

    @Test
    void cached_server_returns_value_quickly() {
        CachedServer server = new CachedServer();
        Timer timer = new Timer();

        server.write("key", "value");

        timer.start();
        String value = server.read("key");
        timer.stop();

        assertEquals("value", value);
        long latencyInSeconds = timer.elapsedSeconds();
        assertTrue(latencyInSeconds < 1, "Latency: "+ latencyInSeconds);
    }

}
