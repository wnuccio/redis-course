package redis;

import redis.clients.jedis.Jedis;

public class ServerWithCacheOnWrite {
    private final SlowServer server;
    private final Jedis cache;

    public ServerWithCacheOnWrite(SlowServer server, Jedis jedis) {
        this.server = server;
        this.cache = jedis;
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

}
