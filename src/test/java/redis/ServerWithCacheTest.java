package redis;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ServerWithCacheTest {

    private ServerWithCache server;
    private Timer timer;

    @BeforeEach
    void setUp() {
        SlowServer server = new SlowServer(4);
        this.server = new ServerWithCache(server, new RedisClientFactory().createClient());
        timer = new Timer();
    }

    @Test
    void cache_on_write_strategy() {
        server.writeAndCache("key1", "value1");

        timer.start();
        String value = server.read("key1");
        timer.stop();

        assertEquals("value1", value);
        long elapsedSeconds = timer.elapsedSeconds();
        assertTrue(elapsedSeconds < 2, "Latency: "+ elapsedSeconds);
    }

    @Test
    void cache_on_read_strategy() {
        server.write("key", "value");

        timer.start();
        String value1 = server.readAndCash("key");
        timer.stop();
        long elapsedSeconds1 = timer.elapsedSeconds();

        timer.start();
        String value2 = server.readAndCash("key");
        timer.stop();
        long elapsedSeconds2 = timer.elapsedSeconds();

        assertEquals("value", value1);
        assertEquals("value", value2);
        assertTrue(elapsedSeconds1 > 3, "Latency: "+ elapsedSeconds1);
        assertTrue(elapsedSeconds2 < 1, "Latency: "+ elapsedSeconds2);
    }
}
