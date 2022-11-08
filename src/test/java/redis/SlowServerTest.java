package redis;

import org.junit.jupiter.api.Test;

public class SlowServerTest {

    @Test
    void server_returns_values_slowly() {
        SlowServer server = new SlowServer(3);

        server.write("key1", "value1");
        server.write("key2", "value2");

        Result result1 = Result.read(() -> server.readSlowly("key1"));
        result1.assertValueReadNotInTime("value1", 2);

        Result result2 = Result.read(() -> server.readSlowly("key2"));
        result2.assertValueReadNotInTime("value2", 2);
    }
}
