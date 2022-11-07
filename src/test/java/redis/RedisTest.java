package redis;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.Jedis;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RedisTest {

    private Jedis jedis;

    @BeforeEach
    void setUp() {
        jedis = jedis();
        jedis.del("prova");
    }

    @AfterEach
    void tearDown() {
        jedis.del("prova");
    }

    @Test
    void set_and_retrieve_value() {
        assertEquals(null, jedis.get("prova"));

        jedis.set("prova", "valore");
        assertEquals("valore", jedis.get("prova"));
    }

    private Jedis jedis() {
        Jedis jedis = new Jedis("redis-19321.c240.us-east-1-3.ec2.cloud.redislabs.com", 19321);
        jedis.auth("7vkXLfZRpRe5fyJ4kFM4wN0UHUJJQ76I");
        return jedis;
    }
}
