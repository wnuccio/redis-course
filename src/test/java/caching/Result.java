package caching;

import redis.Timer;

import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Result {
    private final String value;
    private final long elapsedSeconds;

    public Result(String value, long elapsedSeconds) {
        this.value = value;
        this.elapsedSeconds = elapsedSeconds;
    }

    public static Result read(Supplier<String> readFunction) {
        Timer timer = new Timer();

        timer.start();
        String value = readFunction.get();
        timer.stop();

        return new Result(value, timer.elapsedSeconds());
    }

    public void assertValueReadInTime(String value, int seconds) {
        assertEquals(value, this.value);
        assertTrue(this.elapsedSeconds < seconds, "Latency: " + this.elapsedSeconds);
    }

    public void assertValueReadNotInTime(String value, int seconds) {
        assertEquals(value, this.value);
        assertTrue(this.elapsedSeconds > seconds, "Latency: " + this.elapsedSeconds);
    }
}
