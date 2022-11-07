package redis;

import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

public class Timer {
    public static long latencyInSeconds(LocalTime start, LocalTime end) {
        return Duration.between(end, start).get(ChronoUnit.SECONDS);
    }
}
