package server;

import io.github.cdimascio.dotenv.Dotenv;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class Server {
    private ServerSocket serverSocket;
    private ExecutorService threadPool;
    private AtomicBoolean isRunning = new AtomicBoolean(true);
    private BlockingQueue<String> commandQueue;

    public static void main(String[] args) throws IOException {
        Server server = new Server();
        server.run();
    }

    public Server() throws IOException {
        Dotenv dotenv = Dotenv.load();
        final int PORT = Integer.parseInt(dotenv.get("PORT"));
        this.threadPool = Executors.newCachedThreadPool();
        this.serverSocket = new ServerSocket(PORT);
        this.commandQueue = new ArrayBlockingQueue<>(2);
        ConsumeTask consumeTask = new ConsumeTask(commandQueue);
        for (int i = 0; i < 2; i++) threadPool.execute(consumeTask);
    }

    public void run() throws IOException {
        System.out.println("Server started successfully, waiting for clients...");

        while (isRunning.get()) {
            try {
                Socket socket = serverSocket.accept();
                System.out.println("Accepting client: " + socket.getPort());
                threadPool.execute(new DistributeTask(socket, this, threadPool, commandQueue));
            } catch (SocketException error) {
                System.out.println("The server was closed predictably");
            }
        }
    }

    public void stop() throws IOException {
        System.out.println("Server is shutting down");
        isRunning.set(false);
        serverSocket.close();
        threadPool.shutdown();
    }
}
