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
        waitSomeMilliseconds(i*1000);
    }

    public static void waitSomeMilliseconds(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
