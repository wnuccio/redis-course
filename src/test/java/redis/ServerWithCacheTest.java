package redis;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.Jedis;

public class ServerWithCacheTest {

    private ServerWithCache server;

    @BeforeEach
    void setUp() {
        SlowServer server = new SlowServer(4);
        Jedis jedis = new RedisClientFactory().createClient();
        jedis.flushDB(); // empties the database completely
        this.server = new ServerWithCache(server, jedis);
    }

    @Test
    void read_quickly_only_cached_values_with_cache_on_write_strategy() {
        server.writeAndCache("key1", "value1");
        server.write("key2", "value2");

        Result result1 = Result.read(() -> server.read("key1"));
        result1.assertValueReadInTime("value1", 2);

        Result result2 = Result.read(() -> server.read("key2"));
        result2.assertValueReadNotInTime("value2", 3);
    }

    @Test
    void read_quickly_only_the_second_time_with_cache_on_read_strategy() {
        server.write("key", "value");

        Result result1 = Result.read(() -> server.readAndCash("key"));
        result1.assertValueReadNotInTime("value", 3);

        Result result2 = Result.read(() -> server.readAndCash("key"));
        result2.assertValueReadInTime("value", 1);
    }
}
