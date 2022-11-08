package redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.SetParams;

import java.util.Map;

public class CachedServer {
    private final Server server;
    private final Jedis cache;

    public CachedServer(Server server, RedisClientFactory clientFactory) {
        this.server = server;
        this.cache = clientFactory.createClient();
        this.cache.flushDB(); // empties the database completely
    }

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
}
