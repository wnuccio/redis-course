package redis;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;
import redis.clients.jedis.params.SetParams;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class RedisClientTest {

    private Jedis redis;

    @BeforeEach
    void setUp() {
        redis = new RedisClientFactory().loginToRedisAndFlushDb();
    }

    @Test
    void redis_returns_different_values_for_different_keys() {
        redis.set("key1", "value1");
        redis.set("key2", "value2");

        String value1 = redis.get("key1");
        String value2 = redis.get("key2");

        assertEquals("value1", value1);
        assertEquals("value2", value2);
    }

    @Test
    void redis_removes_values_after_expiration_time() {
        redis.set("key1", "value1");
        redis.set("key2", "value2", SetParams.setParams().ex(1));

        Timer.waitSomeSeconds(1);

        assertEquals("value1", redis.get("key1"));
        assertNull(redis.get("key2"));
    }

    @Test
    void redis_returns_hash_values() {
        redis.hset("hashkey", Map.of("subkey1", "subvalue1", "subkey2", "subvalue2"));

        Map<String, String> allValues = redis.hgetAll("hashkey");
        assertEquals(Map.of("subkey1", "subvalue1", "subkey2", "subvalue2"), allValues);

        String subvalue1 = redis.hget("hashkey", "subkey1");
        String subvalue2 = redis.hget("hashkey", "subkey2");
        assertEquals("subvalue1", subvalue1);
        assertEquals("subvalue2", subvalue2);
    }

    @Test
    void redis_returns_empty_map_for_a_missing_hash() {
        Map<String, String> allValues = redis.hgetAll("key");
        String value = redis.hget("key", "subkey");

        assertEquals(Collections.emptyMap(), allValues);
        assertNull(value);
    }

    @Test
    void read_multiple_keys_with_a_pipeline() {
        redis.set("key1", "value1");
        redis.set("key2", "value2");

        Pipeline pipeline = redis.pipelined();
        Response<String> resp1 = pipeline.get("key1");
        Response<String> resp2 = pipeline.get("key2");
        pipeline.sync();

        assertEquals("value1", resp1.get());
        assertEquals("value2", resp2.get());
    }

    @Test
    void add_and_retrieves_members_in_a_set() {
        redis.sadd("colors", "red", "blue", "green");
        redis.sadd("colors", "blue");

        Set<String> members = redis.smembers("colors");

        assertEquals(3, members.size());
        assertTrue(members.contains("red"));
        assertTrue(members.contains("blue"));
        assertTrue(members.contains("green"));
    }
}
