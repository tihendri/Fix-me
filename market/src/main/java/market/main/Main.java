package market.main;

import market.server.ProcessThread;

public class Main {
    public static int quantity;
    public static int price;
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java -jar [path] [price] [quantity]");
            System.exit(0);
        }
        try {
            quantity = Integer.parseInt(args[0]);
            price = Integer.parseInt(args[1]);
        } catch (Exception e) {
            System.out.println((char)27 + "[0;31mError: You must specify valid numbers" + (char)27 + "[0m");
            System.out.println("Usage: java -jar [path] [price] [quantity]");
            System.exit(0);
        }
        if (quantity < 1) {
            System.out.println((char)27 + "[0;31mError: The quantity must be 1 or more." + (char)27 + "[0m");
            System.exit(0);
        } else if (price < 0) {
            System.out.println((char)27 + "[0;31mError: The price must be positive." + (char)27 + "[0m");
            System.exit(0);
        }
        ProcessThread processThread = new ProcessThread();
        processThread.processThread();
    }
}
