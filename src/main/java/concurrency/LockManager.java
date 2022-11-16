package concurrency;

import redis.Timer;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.SetParams;

class LockManager {
    public static void acquireLockAndRun(Jedis redis, String lockKey, Runnable runnable) {
        int retries = 10;
        while (retries >= 0) {
            retries--;

            boolean executed = tryToExecute(redis, lockKey, runnable);
            if (executed) {
                break;
            } else {
                Timer.waitSomeMilliseconds(100);
            }
        }
    }

    private static boolean tryToExecute(Jedis redis, String lockKey, Runnable runnable) {
        try {
            String lockAcquired = redis.set(lockKey, "lock", new SetParams().nx());

            if (lockAcquired == null)
                return false;

            runnable.run();
            return true;

        } finally {
            // releas the lock in any case
            redis.del(lockKey);
        }
    }
}
