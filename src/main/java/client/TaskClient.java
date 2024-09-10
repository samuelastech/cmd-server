package client;

import io.github.cdimascio.dotenv.Dotenv;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class TaskClient {
    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.load();
        final int PORT = Integer.parseInt(dotenv.get("PORT"));
        final String HOST = dotenv.get("HOST");
        try {
            Socket socket = new Socket(HOST, PORT);
            System.out.println("Connection established with the server");

            Thread threadRead = new Thread(() -> TaskClient.readKeyboard(socket));
            Thread threadListen = new Thread(() -> TaskClient.listen(socket));
            threadListen.start();
            threadRead.start();
            threadRead.join();

            socket.close();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static void listen(Socket socket) {
        try {
            Scanner serverResponse = new Scanner(socket.getInputStream());
            while (serverResponse.hasNextLine()) {
                String output = serverResponse.nextLine();
                System.out.println("Server message: " + output);
            }

            serverResponse.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private static void readKeyboard(Socket socket) {
        try {
            PrintStream output = new PrintStream(socket.getOutputStream());
            System.out.println("You can type commands");
            Scanner keyboard = new Scanner(System.in);

            while (keyboard.hasNextLine()) {
                String input = keyboard.nextLine();
                if (input.trim().isEmpty()) break;
                output.println(input);
            }

            keyboard.close();
            output.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
