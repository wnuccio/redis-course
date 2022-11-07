package redis;

import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CachedServerTest {

    @Test
    void cached_server_returns_value_quickly() {
        Server server = new Server(10);
        CachedServer cachedServer = new CachedServer(server);
        Timer timer = new Timer();

        cachedServer.write("key", "value");

        timer.start();
        String value = cachedServer.read("key");
        timer.stop();

        assertEquals("value", value);
        long elapsedSeconds = timer.elapsedSeconds();
        assertTrue(elapsedSeconds < 1, "Latency: "+ elapsedSeconds);
    }

}
