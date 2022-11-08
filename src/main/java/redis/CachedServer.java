package redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.SetParams;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class CachedServer {
    private final Server server;
    private final Jedis cache;

    public CachedServer(Server server, RedisClientFactory clientFactory) {
        this.server = server;
        this.cache = clientFactory.createClient();
        this.cache.flushDB(); // empties the database completely
    }

    // cache on write strategy
    public void write(String key, String value) {
        cache.set(key, value);
        server.write(key, value);
    }

    public void write(String key, String value, int expirationTimeInSeconds) {
        cache.set(key, value, SetParams.setParams().ex(expirationTimeInSeconds));
        server.write(key, value);
    }

    public String read(String key) {
        String cachedValue = cache.get(key);
        if (cachedValue != null)
            return cachedValue;

        return server.read(key);
    }

    ////////////// Hash /////////////////
    public void writeHash(String hashKey, Map<String, String> subkeys) {
        cache.hset(hashKey, subkeys);
    }

    public String readHash(String hashKey, String subkey) {
        return cache.hget(hashKey, subkey);
    }


    public Map<String, String> readAllHash(String key) {
        return cache.hgetAll(key);
    }

    //////////// Cash on read strategy ////////////
    public void writeNoCash(String key, String value) {
        server.write(key, value);
    }

    public String readAndCash(String key) {
        String cachedValue = cache.get(key);
        if (cachedValue != null)
            return cachedValue;

        String value = server.read(key);
        cache.set(key, value);
        return value;
    }

    ///////////// Numbers ///////////////////
    public void writeNum(String key, int number) {
        cache.set(key, String.valueOf(number));
    }

    public int readNum(String key) {
        return Integer.parseInt(cache.get(key));
    }

    public void writeDate(String key, LocalDate date) {
        cache.set(key, date.format(DateTimeFormatter.ISO_LOCAL_DATE));
    }

    public LocalDate readDate(String key) {
        return LocalDate.parse(cache.get(key), DateTimeFormatter.ISO_LOCAL_DATE);
    }
}
