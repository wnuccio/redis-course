package redis;

import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CachedServerTest {

    @Test
    void cached_server_returns_value_quickly() {
        Server server = new Server(10);
        CachedServer cachedServer = new CachedServer(server);
        Timer timer = new Timer();

        cachedServer.write("key", "value");

        timer.start();
        String value = cachedServer.read("key");
        timer.stop();

        assertEquals("value", value);
        long elapsedSeconds = timer.elapsedSeconds();
        assertTrue(elapsedSeconds < 1, "Latency: "+ elapsedSeconds);
    }

    @Test
    void cached_server_returns_different_values_for_different_keys() {
        Server server = new Server(0);
        CachedServer cachedServer = new CachedServer(server);

        cachedServer.write("key1", "value1");
        cachedServer.write("key2", "value2");

        String value1 = cachedServer.read("key1");
        String value2 = cachedServer.read("key2");

        assertEquals("value1", value1);
        assertEquals("value2", value2);
    }

    //    @Test
//    void cached_server_remove_values_after_expiration_time() {
//        Server server = new Server(4);
//        CachedServer cachedServer = new CachedServer(server);
//        Timer timer = new Timer();
//
//        cachedServer.writeWithExpiration("key", "value", 2);
//
//        timer.waitSomeSeconds(3);
//
//        timer.start();
//        String value = cachedServer.read("key");
//        timer.stop();
//
//        assertEquals("value", value);
//        long elapsedSeconds = timer.elapsedSeconds();
//        assertTrue(elapsedSeconds > 3, "Latency: "+ elapsedSeconds);
//    }
}
