package router.main;

import router.server.Server;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) {
        if (args.length != 0) {
            System.out.println("Router does not accept any arguments! It will be discarded.");
            System.out.println("Remember next time...");
        }
        try {
            ExecutorService threads = Executors.newCachedThreadPool();
            threads.submit(new Server(5000));
            threads.submit(new Server(5001));
            threads.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
