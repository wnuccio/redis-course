package concurrency;

import org.junit.jupiter.api.Test;
import redis.RedisClientFactory;
import redis.clients.jedis.Jedis;

import static org.assertj.core.api.Assertions.assertThat;

public class RedisLockTest {

    private static class AddSymbolToChain {
        private final Jedis redis;
        private final String key;
        private final String symbol;

        private final String lockKey;

        public AddSymbolToChain(Jedis redis, String key, String symbol, String lockKey) {
            this.redis = redis;
            this.key = key;
            this.symbol = symbol;
            this.lockKey = lockKey;
        }

        private void run() {
            LockManager.acquireLockAndRun(redis, lockKey, () -> {
                        String value = redis.get(key);
                        value += symbol;
                        redis.set(key, value);
                    }
            );
        }
    }

    @Test
    void isolate_transactions_with_explicit_locking() {
        Jedis redis1 = new RedisClientFactory().loginToRedisAndFlushDb();
        Jedis redis2 = new RedisClientFactory().loginToRedisAndFlushDb();

        redis1.set("chain", "start");

        Thread t1 = new Thread(new AddSymbolToChain(redis1, "chain", "#", "lock:chain")::run);
        Thread t2 = new Thread(new AddSymbolToChain(redis2, "chain", "#", "lock:chain")::run);

        t1.start();
        t2.start();

        while (t1.isAlive() || t2.isAlive()) {}

        assertThat(redis1.get("chain")).isEqualTo("start##");
    }
}
