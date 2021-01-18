package router.handlers;

import router.server.Attachment;
import router.table.RoutingTable;
import router.server.WhoIsResponsible;
import router.validation.ChecksumValidation;

import java.io.IOException;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;

public class MessageHandler implements CompletionHandler<Integer, Attachment> {

    private final String startOfHeading;
    public MessageHandler() {
        startOfHeading = "" + (char)1;
    }

    @Override
    public void completed(Integer result, Attachment attachment) {
        if (result == -1) {
            try {
                String port = attachment.server.getLocalAddress().toString().split(":")[1];
                int port1 = Integer.parseInt(port);
                System.out.format(getServerName(port1) + "stopped listening to the client %s%n",
                        attachment.socketAddress);
                attachment.channel.close();
                RoutingTable.removeClient(attachment.channelId);
            } catch (IOException ex) {
                System.out.println("\n" + (char)27 + "[0;31mMarket or Broker has terminated their connection to Router." + (char)27 + "[0m");
//                ex.printStackTrace();
            }
            return;
        }

        if (attachment.toWriteMessage) {
            attachment.byteBuffer.flip();
            int limits = attachment.byteBuffer.limit();
            byte[] bytes = new byte[limits];
            attachment.byteBuffer.get(bytes, 0, limits);
            String msg = new String(bytes, StandardCharsets.UTF_8);
            attachment.message = msg.split(startOfHeading);
            try {
                String port = attachment.server.getLocalAddress().toString().split(":")[1];

                int port1 = Integer.parseInt(port);
                System.out.format("["+ getServerName(port1) +"]Client at  %s  says: %s%n", attachment.socketAddress,
                        msg.replace((char)1, '|'));
            }
            catch(Exception e) {
                e.printStackTrace();
            }
            attachment.toWriteMessage = false;
            attachment.byteBuffer.rewind();
            attachment.byteBuffer.clear();
            byte[] data = msg.getBytes(StandardCharsets.UTF_8);
            attachment.byteBuffer.put(data);
            attachment.byteBuffer.flip();
            if (attachment.channel.isOpen() && RoutingTable.getSize() > 1)
                new ChecksumValidation().checkingTheSum(attachment, WhoIsResponsible.CHECKSUM);
        } else {
            attachment.toWriteMessage = true;
            attachment.byteBuffer.clear();
            attachment.channel.read(attachment.byteBuffer, attachment, this);
        }
    }

    @Override
    public void failed(Throwable exc, Attachment attachment) {
        exc.printStackTrace();
    }

    private String getServerName(int port) {
        if (port == 5000)
            return "Broker Server";
        else
            return "Market Server";
    }
}
