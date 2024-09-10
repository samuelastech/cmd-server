package server;

import server.cmd.C2;

import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.*;

public class DistributeTask implements Runnable {
    private Socket clientSocket;
    private Server server;
    private ExecutorService threadPool;
    private BlockingQueue<String> commandQueue;

    public DistributeTask(Socket clientSocket, Server server, ExecutorService threadPool, BlockingQueue<String> commandQueue) {
        this.clientSocket = clientSocket;
        this.server = server;
        this.threadPool = threadPool;
        this.commandQueue = commandQueue;
    }

    @Override
    public void run() {
        System.out.println("Distributing task to: " + clientSocket.getPort());
        try {
            Scanner clientInput = new Scanner(clientSocket.getInputStream());
            PrintStream serverOutput = new PrintStream(clientSocket.getOutputStream());

            while (clientInput.hasNextLine()) {
                String input = clientInput.nextLine();
                switch (input) {
                    case "c1":
                        serverOutput.println("C1...");
                        this.commandQueue.put(input);
                        System.out.println("C1 recebido");
                        break;
                    case "c2":
                        C2.webService webService = new C2.webService(serverOutput);
                        C2.database database = new C2.database(serverOutput);
                        Future<String> serviceData = this.threadPool.submit(webService);
                        Future<String> databaseData = this.threadPool.submit(database);
                        threadPool.execute(() -> {
                            try {
                                String resultService = serviceData.get(20, TimeUnit.SECONDS);
                                String resultDatabase = databaseData.get(20, TimeUnit.SECONDS);
                                serverOutput.println("Service: " + resultService + ", DB: " + resultDatabase);
                            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                                serviceData.cancel(true);
                                databaseData.cancel(true);
                                throw new RuntimeException(e);
                            }
                        });
                        break;
                    case "finish":
                        serverOutput.println("Server is shutting down");
                        server.stop();
                        break;
                    default:
                        serverOutput.println("Unidentified command");
                        System.out.println(input);
                }
            }

            clientInput.close();
            serverOutput.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
