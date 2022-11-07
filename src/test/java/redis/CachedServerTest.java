package redis;

import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CachedServerTest {

    @Test
    void cached_server_returns_value_quickly() {
        CachedServer server = new CachedServer();

        server.write("key", "value");

        LocalTime start = LocalTime.now();
        String value = server.read("key");
        LocalTime end = LocalTime.now();

        assertEquals("value", value);
        long latencyInSeconds = Timer.latencyInSeconds(end, start);
        assertTrue(latencyInSeconds < 1, "Latency: "+ latencyInSeconds);
    }

}
