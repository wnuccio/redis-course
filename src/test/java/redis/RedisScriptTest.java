package redis;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

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
        String scriptId = redis.scriptLoad("" +
                "local theKey = KEYS[1]" +
                "local theValue = ARGV[1]" +
                "redis.call('SET', theKey, theValue)" +
        "");

        redis.evalsha(scriptId, asList("color"), asList("red"));

        assertThat(redis.get("color")).isEqualTo("red");
    }

    @Test
    void script_written_in_a_file() throws IOException {
        String script = Files.readString(Path.of("./src/test/resources/script.txt"));
        String scriptId = redis.scriptLoad(script);
        Object result = redis.evalsha(scriptId, asList("number"), asList("1"));

        assertThat(result).isEqualTo("2");
    }
}
