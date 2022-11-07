package redis;

import redis.clients.jedis.Jedis;

public class CachedServer {
    private final Server server = new Server();
    private Jedis cache;

    public String read(String key) {
        String cachedValue = cache().get(key);
        if (cachedValue != null)
            return cachedValue;

        return server.read(key);
    }

    public void write(String key, String value) {
        cache().set(key, value);
    }

    private Jedis cache() {
        if (cache == null) {
            cache = new Jedis("redis-19321.c240.us-east-1-3.ec2.cloud.redislabs.com", 19321);
            cache.auth("7vkXLfZRpRe5fyJ4kFM4wN0UHUJJQ76I");
        }
        return cache;
    }

}
