package redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.SetParams;

public class CachedServer {
    private final Server server;
    private Jedis cache;

    public CachedServer(Server server) {
        this.server = server;
    }

    public void delete(String key) {
        cache().del(key);
    }

    public String read(String key) {
        String cachedValue = cache().get(key);
        if (cachedValue != null)
            return cachedValue;

        return server.read(key);
    }

    public void write(String key, String value) {
        cache().set(key, value);
        server.write(key, value);
    }

    public void writeWithExpiration(String key, String value, int expirationTimeInSeconds) {
        cache().set(key, value, SetParams.setParams().ex(expirationTimeInSeconds));
        server.write(key, value);
    }

    private Jedis cache() {
        if (cache == null) {
            cache = new Jedis("redis-19321.c240.us-east-1-3.ec2.cloud.redislabs.com", 19321);
            cache.auth("7vkXLfZRpRe5fyJ4kFM4wN0UHUJJQ76I");
        }
        return cache;
    }
}
