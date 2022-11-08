package redis;

import redis.clients.jedis.Jedis;

public class ServerWithCache {
    private final SlowServer server;
    private final Jedis cache;

    public ServerWithCache(SlowServer server, Jedis jedis) {
        this.server = server;
        this.cache = jedis;
        this.cache.flushDB(); // empties the database completely
    }

    public void write(String key, String value) {
        server.write(key, value);
    }

    public void writeAndCache(String key, String value) {
        server.write(key, value);
        cache.set(key, value);
    }

    public String read(String key) {
        String cachedValue = cache.get(key);
        if (cachedValue != null)
            return cachedValue;

        return server.read(key);
    }

    public String readAndCash(String key) {
        String cachedValue = cache.get(key);
        if (cachedValue != null)
            return cachedValue;

        String value = server.read(key);
        cache.set(key, value);
        return value;
    }
}
