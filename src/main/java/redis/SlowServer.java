package redis;

import java.util.HashMap;
import java.util.Map;

public class SlowServer {
    private final Map<String, String> map = new HashMap<>();
    private final int delayInSeconds;

    public SlowServer(int delayInSeconds) {
        this.delayInSeconds = delayInSeconds;
    }

    public void write(String key, String value) {
        map.put(key, value);
    }

    public String readSlowly(String key) {
        String result = map.get(key);

        if (result == null)
            throw new IllegalArgumentException("Invalid key: " + key);

        Timer.waitSomeSeconds(delayInSeconds);
        return result;
    }
}
