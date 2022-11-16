package redis;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.Jedis;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

public class RedisScriptTest {

    private Jedis redis;

    @BeforeEach
    void setUp() {
        redis = new RedisClientFactory().loginToRedisAndFlushDb();
    }

    @Test
    void script_to_return_the_sum_of_two_arguments() {
        String scriptId = redis.scriptLoad("return ARGV[1] + ARGV[2]");

        Object result = redis.evalsha(scriptId, asList(""), asList("1", "2"));

        assertThat(result).isEqualTo(3L);
    }

    @Test
    void script_that_uses_KEYS_and_ARGV_arrays() {
        // Important: any 'key' inside the script must be specified within the KEYS array
        String scriptId = redis.scriptLoad("redis.call('SET', KEYS[1], ARGV[1])");

        redis.evalsha(scriptId, asList("key"), asList("value"));

        assertThat(redis.get("key")).isEqualTo("value");
    }
}
