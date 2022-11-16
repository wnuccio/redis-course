package concurrency;

import redis.Timer;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.SetParams;

class LockManager {
    public static void acquireLockAndRun(Jedis redis, String lockKey, Runnable runnable) {
        int retries = 10;
        while (retries >= 0) {
            retries--;

            String lockAcquired = redis.set(lockKey, "lock", new SetParams().nx());
            if (lockAcquired != null) {
                runnable.run();
                redis.del(lockKey);
                break;
            } else {
                Timer.waitSomeMilliseconds(100);
            }
        }
    }
}
