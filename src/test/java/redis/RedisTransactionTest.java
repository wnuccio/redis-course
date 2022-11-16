package redis;

import org.junit.jupiter.api.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import static org.assertj.core.api.Assertions.assertThat;

public class RedisTransactionTest {

    @Test
    void a_transaction_is_canceled_if_a_concurrent_update_occurs() {
        // a different client for each thread
        Jedis redis1 = new RedisClientFactory().loginToRedisAndFlushDb();
        Jedis redis2 = new RedisClientFactory().loginToRedisAndFlushDb();

        // watching-thread 1: this writes the value 1 only if it is not modified by some other trhead
        Thread thread1 = new Thread(() -> {
            redis1.watch("key");
            System.out.println("thread1 starts watching 'key'");

            Timer.waitSomeMilliseconds(500);

            Transaction transaction = redis1.multi();
            transaction.set("key", "value 1");
            transaction.exec();
            System.out.println("thread1 executes the transaction, trying to write 'key = value 1'");
        });

        // no-watching thread 2: this always writes the value 2, so it wins
        Thread thread2 = new Thread(() -> {
            Timer.waitSomeMilliseconds(500);

            redis2.set("key", "value 2");
            System.out.println("thread2 writes 'key = value 2'");
        });

        thread1.start();
        thread2.start();

        while (thread1.isAlive() || thread2.isAlive()) {}

        assertThat(redis1.get("key")).isEqualTo("value 2");
    }
}
