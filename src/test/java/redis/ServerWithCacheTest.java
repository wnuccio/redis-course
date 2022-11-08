package redis;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.Jedis;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ServerWithCacheTest {

    private ServerWithCache server;
    private Timer timer;

    @BeforeEach
    void setUp() {
        SlowServer server = new SlowServer(4);
        Jedis jedis = new RedisClientFactory().createClient();
        jedis.flushDB(); // empties the database completely
        this.server = new ServerWithCache(server, jedis);
        timer = new Timer();
    }

    @Test
    void read_quickly_only_cached_values_with_cache_on_write_strategy() {
        server.writeAndCache("key1", "value1");
        server.write("key2", "value2");

        timer.start();
        String value1 = server.read("key1");
        timer.stop();
        long elapsedSeconds1 = timer.elapsedSeconds();

        timer.start();
        String value2 = server.read("key2");
        timer.stop();
        long elapsedSeconds2 = timer.elapsedSeconds();

        assertEquals("value1", value1);
        assertTrue(elapsedSeconds1 < 2, "Latency: "+ elapsedSeconds1);

        assertEquals("value2", value2);
        assertTrue(elapsedSeconds2 > 3, "Latency: "+ elapsedSeconds2);
    }

    @Test
    void read_quickly_only_the_second_time_with_cache_on_read_strategy() {
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
        assertTrue(elapsedSeconds1 > 3, "Latency: "+ elapsedSeconds1);

        assertEquals("value", value2);
        assertTrue(elapsedSeconds2 < 1, "Latency: "+ elapsedSeconds2);
    }
}
