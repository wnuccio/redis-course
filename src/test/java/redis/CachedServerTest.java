package redis;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class CachedServerTest {

    private CachedServer cachedServer;
    private Timer timer;

    @BeforeEach
    void setUp() {
        Server server = new Server(4);
        RedisClientFactory redisClient = new RedisClientFactory();
        cachedServer = new CachedServer(server, redisClient);
        timer = new Timer();
    }

    @Test
    void cached_server_returns_value_quickly() {
        cachedServer.write("key1", "value1");

        timer.start();
        String value = cachedServer.read("key1");
        timer.stop();

        assertEquals("value1", value);
        long elapsedSeconds = timer.elapsedSeconds();
        assertTrue(elapsedSeconds < 2, "Latency: "+ elapsedSeconds);
    }

    @Test
    void cached_server_returns_different_values_for_different_keys() {
        cachedServer.write("key1", "value1");
        cachedServer.write("key2", "value2");

        String value1 = cachedServer.read("key1");
        String value2 = cachedServer.read("key2");

        assertEquals("value1", value1);
        assertEquals("value2", value2);
    }

    @Test
    void cached_server_remove_values_after_expiration_time() {
        cachedServer.write("key1", "value1");
        cachedServer.write("key2", "value2", 1);

        timer.waitSomeSeconds(1);

        timer.start();
        String value1 = cachedServer.read("key1");
        timer.stop();
        long elapsedSeconds1 = timer.elapsedSeconds();

        timer.start();
        String value2 = cachedServer.read("key2");
        timer.stop();
        long elapsedSeconds2 = timer.elapsedSeconds();

        assertEquals("value1", value1);
        assertEquals("value2", value2);
        assertTrue(elapsedSeconds1 < 1, "Latency: "+ elapsedSeconds1);
        assertTrue(elapsedSeconds2 > 3, "Latency: "+ elapsedSeconds2);
    }

    @Test
    void cached_server_retrieve_hash_values() {
        cachedServer.writeHash("hashkey", Map.of("subkey1", "subvalue1", "subkey2", "subvalue2"));

        Map<String, String> allValues = cachedServer.readAllHash("hashkey");
        assertEquals(Map.of("subkey1", "subvalue1", "subkey2", "subvalue2"), allValues);

        String subvalue1 = cachedServer.readHash("hashkey", "subkey1");
        String subvalue2 = cachedServer.readHash("hashkey", "subkey2");
        assertEquals("subvalue1", subvalue1);
        assertEquals("subvalue2", subvalue2);
    }

    @Test
    void cached_server_return_empty_map_for_a_missing_hash() {
        Map<String, String> allValues = cachedServer.readAllHash("key");
        String value = cachedServer.readHash("key", "subkey");

        assertEquals(Collections.emptyMap(), allValues);
        assertNull(value);
    }

    @Test
    void write_withouth_caching_and_cache_on_first_read() {
        cachedServer.writeNoCash("key", "value");

        timer.start();
        String value1 = cachedServer.readAndCash("key");
        timer.stop();
        long elapsedSeconds1 = timer.elapsedSeconds();

        timer.start();
        String value2 = cachedServer.readAndCash("key");
        timer.stop();
        long elapsedSeconds2 = timer.elapsedSeconds();

        assertEquals("value", value1);
        assertEquals("value", value2);
        assertTrue(elapsedSeconds1 > 3, "Latency: "+ elapsedSeconds1);
        assertTrue(elapsedSeconds2 < 1, "Latency: "+ elapsedSeconds2);
    }

    @Test
    void write_and_retrieve_a_number() {
        cachedServer.writeNum("key", 1234);

        int num = cachedServer.readNum("key");

        assertEquals(1234, num);
    }

    @Test
    void write_and_retrieve_a_date() {
        LocalDate date = LocalDate.now();
        cachedServer.writeDate("key", date);

        LocalDate retrievedDate = cachedServer.readDate("key");

        assertEquals(date, retrievedDate);
    }
}
