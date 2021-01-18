package market.handlers;

import market.main.Main;
import market.server.ProcessThread;

import java.util.Random;

public class MessageHandler {
    private static final String startOfHeading = "" + (char)1;
    private static int dstId;
    private static int request;
    private static final String FIX_4_2 = "8=FIX.4.2";

    public static String breakdownMessage(String message) {
        String[] data = message.split(startOfHeading);
        String FIX_tag_35="type of message";
        String FIX_tag_54="buy(1) or sell(2)";
        String FIX_tag_44="price";
        String FIX_tag_38="order quantity";
        int i = 0;
        if (i < data.length) {
            do {
                String dat = data[i];
                if (dat.contains("35="))
                    FIX_tag_35 = dat.split("=")[1];
                else if (dat.contains("54="))
                    FIX_tag_54 = dat.split("=")[1];
                else if (dat.contains("44="))
                    FIX_tag_44 = dat.split("=")[1];
                else if (dat.contains("38="))
                    FIX_tag_38 = dat.split("=")[1];
                else if (dat.contains("id="))
                    dstId = Integer.parseInt(dat.split("=")[1]);
                i++;
            } while (i < data.length);
        }
        return processMessage(FIX_tag_35, FIX_tag_54, FIX_tag_44, FIX_tag_38);
    }

    private static String processMessage(String FIX_tag_35, String FIX_tag_54, String FIX_tag_44, String FIX_tag_38) {
        setRequest();
        int p = Integer.parseInt(FIX_tag_44);
        int q = Integer.parseInt(FIX_tag_38);
        if (FIX_tag_35.equals("D") && FIX_tag_54.equals("2") && p < Main.price && (request == 2 || request == 3))
            return getMessage("buy", Integer.parseInt(FIX_tag_38)); //buy from broker
        else if (FIX_tag_35.equals("D") && FIX_tag_54.equals("1") && p >= Main.price && Main.quantity - q >= 0 && (request == 2 || request == 3))
            return getMessage("sell", Integer.parseInt(FIX_tag_38)); //sell to broker
        else
            return getMessage("reject", Integer.parseInt(FIX_tag_38)); //reject broker request
    }

    private static String getMessage(String option, int quantity) {
        String msg = "";
        if (option.equals("reject")) {
            msg = "id=" +
                    ProcessThread.attachment.id +
                    startOfHeading +
                    FIX_4_2 +
                    startOfHeading +
                    "35=8" +
                    startOfHeading +
                    "39=8" +
                    startOfHeading +
                    "50=" +
                    ProcessThread.attachment.id +
                    startOfHeading +
                    "49=" +
                    ProcessThread.attachment.id +
                    startOfHeading +
                    "56=" + dstId +
                    startOfHeading;
        }
        if (option.equals("sell")) {
            msg = "id=" +
                    ProcessThread.attachment.id +
                    startOfHeading +
                    FIX_4_2 +
                    startOfHeading +
                    "35=8" +
                    startOfHeading +
                    "39=2" +
                    startOfHeading +
                    "50=" +
                    ProcessThread.attachment.id +
                    startOfHeading +
                    "49=" +
                    ProcessThread.attachment.id +
                    startOfHeading +
                    "56=" + dstId +
                    startOfHeading;
            Main.quantity -= quantity;
        }
        if (option.equals("buy")) {
            msg = "id=" +
                    ProcessThread.attachment.id +
                    startOfHeading +
                    FIX_4_2 +
                    startOfHeading +
                    "35=8" +
                    startOfHeading +
                    "39=2" +
                    startOfHeading +
                    "50=" +
                    ProcessThread.attachment.id +
                    startOfHeading +
                    "49=" +
                    ProcessThread.attachment.id +
                    startOfHeading +
                    "56=" + dstId +
                    startOfHeading;
            Main.quantity += quantity;
        }
        return msg + getCheckSum(msg);
    }

    private static String getCheckSum(String message) {
        int j = 0;
        char[] t;
        String[] datum = message.split(startOfHeading);
        for (String s : datum) {
            t = s.toCharArray();
            for (char c : t) {
                j += c;
            }
            j += 1;
        }
        return ("CS="+ (j % 256) + startOfHeading);
    }

    private static void setRequest() {
        Random rand = new Random();
        MessageHandler.request = rand.nextInt(3) + 1;
    }
}
