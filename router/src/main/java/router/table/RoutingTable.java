package router.table;

import router.server.Attachment;

import java.util.ArrayList;
import java.util.List;

public class RoutingTable {
    private static final List<Attachment> clients = new ArrayList<>();

    public static void addClient(Attachment client)
    {
        clients.add(client);
    }
    public static Attachment getClient(int id)
    {
        for(Attachment client : clients)
        {
            if (client.channelId == id)
                return client;
        }
        return null;
    }
    public static int getSize()
    {
        return clients.size();
    }
    public static void removeClient(int id)
    {
        try
        {
            clients.remove(getClient(id));
        }
        catch(Exception ignored){}
    }
}
