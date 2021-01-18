package broker.handlers;

import broker.main.Main;
import broker.server.ProcessThread;

public class MessageHandler {
    private static int qty = 9999999;
    private static int cash = 9999999;
    private static final String FIX_4_2 = "8=FIX.4.2";
    private static final String startOfHeading = "" + (char) 1;

    public static String messageHandler() {
        String message;

        if (Main.buyOrSell == 1)
            message = buyProduct(Main.marketID);
        else
            message = sellProduct(Main.marketID);
        assert message != null;
        return message + getCheckSum(message);
    }

    private static String getCheckSum(String msg)
    {
        int j = 0;
        char[] t;
        String[] datum = msg.split(startOfHeading);
        for (String s : datum) {
            t = s.toCharArray();
            for (char c : t) {
                j += c;
            }
            j += 1;
        }
        return ("CS="+ (j % 256) + startOfHeading);
    }

    public static String sellProduct(int receiver)
    {
        String message = "id=" +
                ProcessThread.attachment.id +
                startOfHeading +
                FIX_4_2 +
                startOfHeading +
                "35=D" +
                startOfHeading +
                "54=2" +
                startOfHeading +
                "38=2" +
                startOfHeading +
                "44=55" +
                startOfHeading +
                "55=RTX3080" +
                startOfHeading;
        message += "50=" +
                ProcessThread.attachment.id +
                startOfHeading +
                "49=" +
                ProcessThread.attachment.id +
                startOfHeading +
                "56=" +
                receiver +
                startOfHeading;
        if (qty > 0)
            return message;
        else
            return null;
    }
    public static String buyProduct(int receiver)
    {
        String message = "id=" +
                ProcessThread.attachment.id +
                startOfHeading +
                FIX_4_2 +
                startOfHeading +
                "35=D" +
                startOfHeading +
                "54=1" +
                startOfHeading +
                "38=2" +
                startOfHeading +
                "44=90" +
                startOfHeading +
                "55=RTX3090" +
                startOfHeading;
        message += "50=" +
                ProcessThread.attachment.id +
                startOfHeading +
                "49=" +
                ProcessThread.attachment.id +
                startOfHeading +
                "56=" +
                receiver +
                startOfHeading;
        if (cash > 0)
            return message;
        else
            return null;
    }
    public static boolean processReply(String reply)
    {
        String[] data = reply.split(""+(char)1);
        String tag = "";
        String state = "";
        for(String dat : data)
        {
            if (dat.contains("35="))
                tag = dat.split("=")[1];
            if (dat.contains("39="))
                state = dat.split("=")[1];
        }
        if (tag.equals("8") && state.equals("8"))
        {
            System.out.println("\nMarket[" + Main.marketID +"] rejected order\n");
            return false;
        }
        if (tag.equals("8") && state.equals("2"))
        {
            System.out.println("\nMarket[" + Main.marketID +"] executed order\n");
            return true;
        }
        return false;
    }
    public static void updateData(boolean state)
    {
        if (!state)
        {
            qty -= 2;
            cash += 55;
        }
        else
        {
            qty += 2;
            cash -= 90;
        }
    }
}
