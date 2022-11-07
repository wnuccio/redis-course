package redis;

import java.util.HashMap;
import java.util.Map;

public class Server {
    private final Map<String, String> map = new HashMap<>();
    private final int delayInSeconds;

    public Server(int delayInSeconds) {
        this.delayInSeconds = delayInSeconds;
    }

    public void write(String key, String value) {
        map.put(key, value);
    }

    public String read(String key) {
        String result = map.get(key);

        if (result == null)
            throw new IllegalArgumentException("Invalid key: " + key);

        waitFor(delayInSeconds);
        return result;
    }

    private void waitFor(int seconds) {
        try {
            long milliseconds = seconds * 1000L;
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
