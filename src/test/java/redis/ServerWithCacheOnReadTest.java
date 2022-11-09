package redis;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.Jedis;

public class ServerWithCacheOnReadTest {

    private ServerWithCacheOnRead server;

    @BeforeEach
    void setUp() {
        SlowServer server = new SlowServer(4);
        Jedis jedis = new RedisClientFactory().loginToRedisAndFlushDb();
        this.server = new ServerWithCacheOnRead(server, jedis);
    }

    @Test
    void read_quickly_only_the_second_time() {
        server.write("key", "value");

        Result result1 = Result.read(() -> server.readAndCash("key"));
        result1.assertValueReadNotInTime("value", 3);

        Result result2 = Result.read(() -> server.readAndCash("key"));
        result2.assertValueReadInTime("value", 2);
    }
}
