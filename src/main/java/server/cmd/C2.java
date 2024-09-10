package server.cmd;

import java.io.PrintStream;
import java.util.Random;
import java.util.concurrent.Callable;

public class C2 {
    private static void teste() {}

    public static class webService implements Callable<String> {
        private static PrintStream output;

        public webService(PrintStream output) {
            this.output = output;
        }

        @Override
        public String call() throws Exception {
            var message = "Executing WS C2 command...";
            System.out.println(message);
            output.println(message);
            Thread.sleep(15000);
            output.println("WS C2 executed successfully");
            int random = new Random().nextInt(100) + 1;
            return Integer.toString(random);
        }
    }

    public static class database implements Callable<String> {
        private static PrintStream output;

        public database(PrintStream output) {
            this.output = output;
        }

        @Override
        public String call() throws Exception {
            var message = "Executing database C2 command...";
            System.out.println(message);
            output.println(message);
            Thread.sleep(1500);
            output.println("Database C2 executed successfully");
            int random = new Random().nextInt(100) + 1;
            return Integer.toString(random);
        }
    }
}
