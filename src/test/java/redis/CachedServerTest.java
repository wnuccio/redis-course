package redis;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CachedServerTest {

    private CachedServer cachedServer;
    private Timer timer;

    @BeforeEach
    void setUp() {
        Server server = new Server(4);
        RedisClientFactory redisClient = new RedisClientFactory();
        cachedServer = new CachedServer(server, redisClient);
        timer = new Timer();

        cachedServer.delete("key1");
        cachedServer.delete("key2");
    }

    @Test
    void cached_server_returns_value_quickly() {
        cachedServer.write("key1", "value1");

        timer.start();
        String value = cachedServer.read("key1");
        timer.stop();

        assertEquals("value1", value);
        long elapsedSeconds = timer.elapsedSeconds();
        assertTrue(elapsedSeconds < 2, "Latency: "+ elapsedSeconds);
    }

    @Test
    void cached_server_returns_different_values_for_different_keys() {
        cachedServer.write("key1", "value1");
        cachedServer.write("key2", "value2");

        String value1 = cachedServer.read("key1");
        String value2 = cachedServer.read("key2");

        assertEquals("value1", value1);
        assertEquals("value2", value2);
    }

    @Test
    void cached_server_remove_values_after_expiration_time() {
        cachedServer.write("key1", "value1");
        cachedServer.writeWithExpiration("key2", "value2", 1);

        timer.waitSomeSeconds(1);

        timer.start();
        String value1 = cachedServer.read("key1");
        timer.stop();
        long elapsedSeconds1 = timer.elapsedSeconds();

        timer.start();
        String value2 = cachedServer.read("key2");
        timer.stop();
        long elapsedSeconds2 = timer.elapsedSeconds();

        assertEquals("value1", value1);
        assertEquals("value2", value2);
        assertTrue(elapsedSeconds1 < 1, "Latency: "+ elapsedSeconds1);
        assertTrue(elapsedSeconds2 > 3, "Latency: "+ elapsedSeconds2);
    }
}
