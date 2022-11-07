package redis;

public class Server {
    public String read(String key) {
        waitFor(4000);

        return "value";
    }

    private void waitFor(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
