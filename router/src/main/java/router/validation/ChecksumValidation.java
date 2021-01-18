package router.validation;

import router.server.Attachment;
import router.table.Send;
import router.server.WhoIsResponsible;

public class ChecksumValidation {
    private static final WhoIsResponsible WHO_IS_RESPONSIBLE = WhoIsResponsible.CHECKSUM;

    public void checkingTheSum(Attachment attach, WhoIsResponsible responsible)
    {
        if (responsible != WHO_IS_RESPONSIBLE)
        {
            new Send().sendMessage(attach, responsible);
            return ;
        }
        int size = getMsgSize(attach.message);
        int checksum = getCheckSum(attach.message[attach.message.length - 1]);
        WhoIsResponsible action;
        if (size % 256 != checksum)
            action = WhoIsResponsible.SOURCE;
        else
            action = WhoIsResponsible.DESTINATION;
        new Send().sendMessage(attach, action);
    }

    private int getMsgSize(String[] datum) {
        int j = 0;
        char[] t;
        for(int k = 0; k < datum.length - 1; k++) {
            t = datum[k].toCharArray();
            for (char c : t) {
                j += c;
            }
            j += 1;
        }
        return (j);
    }

    private int getCheckSum(String part) {
        String tag;
        int value;
        try {
            String[] strings = part.split("=");
            tag = strings[0];
            value = Integer.parseInt(strings[1]);
            if (tag.equals("CS"))
                return value;
        }
        catch(Exception e) {
            System.out.println("Error detected: checksum");
        }
        return (0);
    }
}
