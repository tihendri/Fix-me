package broker.main;

import broker.server.ProcessThread;

public class Main {
    public static int marketID;
    public static int buyOrSell;
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java -jar [path] [market ID] [(1)buy or (2)sell]");
            System.exit(0);
        }
        try {
            marketID = Integer.parseInt(args[0]);
            if (Integer.parseInt(args[1]) > 2 || Integer.parseInt(args[1]) <= 0) {
                System.out.println((char)27 + "[0;31mError: You can only specify between option 1 and 2 for second argument." + (char)27 + "[0m");
                System.exit(0);
            } else {
                buyOrSell = Integer.parseInt(args[1]);
            }
        } catch (Exception e) {
            System.out.println((char)27 + "[0;31mError: You must specify valid numbers" + (char)27 + "[0m");
            System.out.println("Usage: java -jar [path] [market ID] [(1)buy or (2)sell]");
            System.exit(0);
        }
        ProcessThread processThread = new ProcessThread();
        processThread.processThread();
    }
}
