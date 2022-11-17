package redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPooled;

public class RedisClientFactory {

    public Jedis loginToRedisAndFlushDb() {
        try {
            Jedis client = new Jedis("redis-12015.c13.us-east-1-3.ec2.cloud.redislabs.com", 12015);
            client.auth("L9a7z7OVTQ103v8wuTaIrZc955H8TTzQ");
            client.flushDB(); // empties the database completely
            return client;

        } catch (RuntimeException e) {
            System.out.println("############ Connessione a Redis fallita ###########");
            System.out.println("####### Verifica l'URL e le credenziali ############");
            System.out.println("####################################################");
            e.printStackTrace();
            throw e;
        }
    }

    public JedisPooled loginToRedisWithModulesAndFlushDb() {
        loginToRedisAndFlushDb();
        try {
            return new JedisPooled("redis-12015.c13.us-east-1-3.ec2.cloud.redislabs.com", 12015,
                    null, "L9a7z7OVTQ103v8wuTaIrZc955H8TTzQ");

        } catch (RuntimeException e) {
            System.out.println("############ Connessione a Redis fallita ###########");
            System.out.println("####### Verifica l'URL e le credenziali ############");
            System.out.println("####################################################");
            e.printStackTrace();
            throw e;
        }
    }

    public static void main(String[] args) {
        // run this main if you want to empty the database
        new RedisClientFactory().loginToRedisAndFlushDb();
    }
}
