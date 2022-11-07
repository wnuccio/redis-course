package redis;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CachedServerTest {

    @Test
    void server_returns_value_slowly() {
        Server server = new Server();

        LocalTime start = LocalTime.now();
        String value = server.read("key");
        LocalTime end = LocalTime.now();

        assertEquals("value", value);
        long latencyInSeconds = latencyInSeconds(end, start);
        assertTrue(latencyInSeconds > 3, "Latency: "+ latencyInSeconds);
    }

    private long latencyInSeconds(LocalTime start, LocalTime end) {
        return Duration.between(end, start).get(ChronoUnit.SECONDS);
    }
}
