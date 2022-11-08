package redis;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.SetParams;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class RedisClientTest {

    private Jedis redis;

    @BeforeEach
    void setUp() {
        redis = new RedisClientFactory().createClient();
        redis.flushDB();
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
    void redis_writes_and_returns_a_number() {
        redis.set("key", Integer.valueOf(1234).toString());

        int num = Integer.parseInt(redis.get("key"));

        assertEquals(1234, num);
    }

    @Test
    void write_and_retrieve_a_date() {
        LocalDate date = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;

        redis.set("key", date.format(formatter));

        LocalDate retrievedDate = LocalDate.parse(redis.get("key"), formatter);

        assertEquals(date, retrievedDate);
    }
}
