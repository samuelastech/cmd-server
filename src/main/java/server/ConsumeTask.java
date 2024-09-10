package server;

import java.util.concurrent.BlockingQueue;

public class ConsumeTask implements Runnable {
    private BlockingQueue<String> commandQueue;

    public ConsumeTask(BlockingQueue<String> commandQueue) {
        this.commandQueue = commandQueue;
    }

    @Override
    public void run() {
        try {
            String command = null;
            while ((command = commandQueue.take()) != null) {
                System.out.println("Command is being consumed: " + command + ", " + Thread.currentThread().getName());
                Thread.sleep(5000);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
