package redis;

import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

public class Timer {
    private LocalTime start;
    private LocalTime end;

    public void start() {
        start = LocalTime.now();
    }

    public void stop() {
        end = LocalTime.now();
    }

    public long elapsedSeconds() {
        return Duration.between(start, end).get(ChronoUnit.SECONDS);
    }

    public static void waitSomeSeconds(int i) {
        try {
            Thread.sleep(i * 1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
