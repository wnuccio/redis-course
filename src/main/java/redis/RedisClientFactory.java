package redis;

import redis.clients.jedis.Jedis;

public class RedisClientFactory {

    public Jedis createClient() {
        Jedis client = new Jedis("redis-19321.c240.us-east-1-3.ec2.cloud.redislabs.com", 19321);
        client.auth("7vkXLfZRpRe5fyJ4kFM4wN0UHUJJQ76I");
        return client;
    }
}
