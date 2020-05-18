package sdk;

public class SimpleForeverThread extends Thread {
    @Override
    public void run() {
        while (!currentThread().isInterrupted()) {
            try {
                sleep(Long.MAX_VALUE);
            } catch (InterruptedException e) {
                Thread.currentThread().stop();
            }
        }
    }
}
