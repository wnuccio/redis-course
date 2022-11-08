package redis;

import redis.clients.jedis.Jedis;

public class ServerWithCacheOnRead {
    private final SlowServer server;
    private final Jedis cache;

    public ServerWithCacheOnRead(SlowServer server, Jedis jedis) {
        this.server = server;
        this.cache = jedis;
    }

    public void write(String key, String value) {
        server.write(key, value);
    }

    public String readAndCash(String key) {
        String cachedValue = cache.get(key);
        if (cachedValue != null)
            return cachedValue;

        String value = server.readSlowly(key);
        cache.set(key, value);
        return value;
    }
}
