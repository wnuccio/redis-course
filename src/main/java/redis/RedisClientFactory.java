package redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPooled;

public class RedisClientFactory {

    private final String host = "redis-12015.c13.us-east-1-3.ec2.cloud.redislabs.com";
    private final int port = 12015;
    private final String password = "L9a7z7OVTQ103v8wuTaIrZc955H8TTzQ";

    public Jedis loginToRedisAndFlushDb() {
        try {
            Jedis client = new Jedis(host, port);
            client.auth(password);
            client.flushDB(); // empties the database completely
            return client;

        } catch (RuntimeException e) {
            printError(e);
            throw e;
        }
    }

    public JedisPooled loginToRedisWithModulesAndFlushDb() {
        loginToRedisAndFlushDb();
        try {
            return new JedisPooled(host, port, null, password);

        } catch (RuntimeException e) {
            printError(e);
            throw e;
        }
    }

    private void printError(RuntimeException e) {
        System.out.println("############ Connessione a Redis fallita ###########");
        System.out.println("####### Verifica l'URL e le credenziali ############");
        System.out.println("####################################################");
        e.printStackTrace();
    }

    public static void main(String[] args) {
        // run this main if you want to empty the database
        new RedisClientFactory().loginToRedisAndFlushDb();
    }
}
