package redis;

public class Server {

    private final int delayInSeconds;

    public Server(int delayInSeconds) {
        this.delayInSeconds = delayInSeconds;
    }

    public String read(String key) {
        waitFor(delayInSeconds);

        return "value";
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
