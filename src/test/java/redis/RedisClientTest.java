package redis;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;
import redis.clients.jedis.params.SetParams;
import redis.clients.jedis.params.SortingParams;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
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
    void add_and_retrieves_members_in_a_set_by_ensuring_uniqueness() {
        redis.sadd("colors", "red", "blue", "green");
        redis.sadd("colors", "blue");

        Set<String> members = redis.smembers("colors");

        assertEquals(3, members.size());
        assertTrue(members.contains("red"));
        assertTrue(members.contains("blue"));
        assertTrue(members.contains("green"));
    }

    @Test
    void operations_on_sets() {
        redis.sadd("colors1", "red", "green");
        redis.sadd("colors2", "green", "blue");

        Set<String> union = redis.sunion("colors1", "colors2");
        Set<String> intersection = redis.sinter("colors1", "colors2");
        Set<String> difference = redis.sdiff("colors1", "colors2");

        assertThat(union).containsExactlyInAnyOrder("red", "green", "blue");
        assertThat(intersection).containsExactlyInAnyOrder("green");
        assertThat(difference).containsExactlyInAnyOrder("red");
    }

    @Test
    void sorted_set_of_values() {
        redis.zadd("key", 2, "value2");
        redis.zadd("key", 1, "value1");
        redis.zadd("key", 3, "value3");

        List<String> all = redis.zrange("key", 0, 2);

        assertThat(all).containsExactly("value1", "value2", "value3");
    }

    @Test
    void retrieves_all_users_by_manually_joining_an_id_set_with_single_hashes_throughout_a_pipeline() {
        redis.hset("users#1", Map.of("name", "Pippo", "age", "35"));
        redis.hset("users#2", Map.of("name", "Pluto", "age", "30"));
        redis.hset("users#3", Map.of("name", "Minni", "age", "40"));

        redis.sadd("user:ids", "1");
        redis.sadd("user:ids", "2");
        redis.sadd("user:ids", "3");

        Set<String> userIds = redis.smembers("user:ids");

        // retrieves all users with a pipeline of hgetAll
        Pipeline pipeline = redis.pipelined();
        List<Response<Map<String, String>>> userResponses = userIds.stream()
                .map(userId -> pipeline.hgetAll("users" + "#" + userId))
                .collect(Collectors.toList());
        pipeline.sync();
        List<String> userNames = userResponses.stream()
                .map(Response::get)
                .map(userHash -> userHash.get("name"))
                .collect(Collectors.toList());

        assertThat(userNames).containsExactlyInAnyOrder("Pippo", "Pluto", "Minni");
    }

    @Test
    void retrieves_all_users_by_joining_an_id_set_with_single_hashes_throughout_the_sort_command() {
        redis.hset("users:1", Map.of("name", "Pippo", "age", "35"));
        redis.hset("users:2", Map.of("name", "Pluto", "age", "30"));
        redis.hset("users:3", Map.of("name", "Minni", "age", "40"));

        // add ids in no particular order
        redis.sadd("user:ids", "3", "2", "1");

        // order ids naturally
        List<String> userIds = redis.sort("user:ids");
        assertThat(userIds).containsExactly("1", "2", "3");

        // order ids by the 'age' field
        List<String> usersIdSortedByAge = redis.sort("user:ids", new SortingParams().by("users:*->age"));
        assertThat(usersIdSortedByAge).containsExactly("2", "1", "3");

        // order ids by the 'name' field
        List<String> userIdsSortedByName = redis.sort("user:ids", new SortingParams().by("users:*->name").alpha());
        assertThat(userIdsSortedByName).containsExactly("3", "1", "2");

        // order by the 'age' field and then take the 'name' field
        List<String> userNamesSortedByAge = redis.sort("user:ids", new SortingParams().by("users:*->age").get("users:*->name"));
        assertThat(userNamesSortedByAge).containsExactly("Pluto", "Pippo", "Minni");

        // order by the 'age' field and then take both the 'name' and the 'age'
        List<String> usersWithIdAndProperties = redis.sort("user:ids", new SortingParams()
                .by("users:*->age")
                .get("#", "users:*->name", "users:*->age"));
        assertThat(usersWithIdAndProperties).containsExactly(
                "2", "Pluto", "30",
                "1", "Pippo", "35",
                "3", "Minni", "40");
    }
}
