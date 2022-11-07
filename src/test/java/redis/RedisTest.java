package redis;

import org.junit.jupiter.api.Test;
import redis.clients.jedis.Jedis;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RedisTest {
    @Test
    void name() {
        Jedis jedis = jedis();

        assertEquals(null, jedis.get("prova"));
    }

    private Jedis jedis() {
        Jedis jedis = new Jedis("redis-19321.c240.us-east-1-3.ec2.cloud.redislabs.com", 19321);
        jedis.auth("7vkXLfZRpRe5fyJ4kFM4wN0UHUJJQ76I");
        return jedis;
    }
}
