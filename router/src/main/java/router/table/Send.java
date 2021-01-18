package router.table;

import router.server.Attachment;
import router.server.WhoIsResponsible;

public class Send {
    private static final WhoIsResponsible WHO_IS_RESPONSIBLE = WhoIsResponsible.DESTINATION;

    public void sendMessage(Attachment attachment, WhoIsResponsible responsible)
    {
        if (responsible != WHO_IS_RESPONSIBLE)
        {
            new Return().returnMessage(attachment, responsible);
            return ;
        }
        int id = getDestination(attachment.message);
        int srcId = getSource(attachment.message);
        if (srcId != attachment.channelId)
        {
            System.out.println("src = " + srcId + " clientId = "+ attachment.channelId);
            new Return().returnMessage(attachment, WhoIsResponsible.SOURCE);
            return ;
        }
        try
        {
            if (attachment.channel.isOpen() && RoutingTable.getSize() > 1)
            {
                Attachment att = RoutingTable.getClient(id);
                if (att == null)
                {
                    new Return().returnMessage(attachment, WhoIsResponsible.SOURCE);
                    return ;
                }
                att.toWriteMessage = false;
                att.channel.write(attachment.byteBuffer, att, attachment.messageHandler);
            }
        }
        catch(Exception e)
        {
            new Return().returnMessage(attachment, WhoIsResponsible.SOURCE);
        }
    }
    private int getDestination(String[] datum)
    {
        try
        {
            for (String s : datum) {
                if (s.contains("56"))
                    return Integer.parseInt(s.split("=")[1]);
            }
        }
        catch(Exception ignored) {}
        return -1;
    }
    private int getSource(String[] datum)
    {
        String id = null;
        try
        {
            if (datum[0].split("=")[0].equalsIgnoreCase("id"))
                id = datum[0].split("=")[1];
            assert id != null;
            return Integer.parseInt(id);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
}
