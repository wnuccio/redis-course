package caching;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import redis.RedisClientFactory;
import redis.ServerWithCacheOnWrite;
import redis.SlowServer;
import redis.clients.jedis.Jedis;

public class ServerWithCacheOnWriteTest {

    private ServerWithCacheOnWrite server;

    @BeforeEach
    void setUp() {
        SlowServer server = new SlowServer(4);
        Jedis jedis = new RedisClientFactory().loginToRedisAndFlushDb();
        this.server = new ServerWithCacheOnWrite(server, jedis);
    }

    @Test
    void read_quickly_all_written_values() {
        server.writeAndCache("key1", "value1");
        server.writeAndCache("key2", "value2");

        Result result1 = Result.read(() -> server.read("key1"));
        result1.assertValueReadInTime("value1", 2);

        Result result2 = Result.read(() -> server.read("key2"));
        result2.assertValueReadInTime("value2", 2);
    }
}
