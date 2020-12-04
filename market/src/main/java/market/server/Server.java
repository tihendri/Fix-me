package market.server;

import market.attachment.Attachment;
import market.handlers.MessageHandler;

import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;

public class Server implements CompletionHandler<Integer, Attachment> {
    @Override
    public void completed(Integer result, Attachment attachment) {
        if (result == -1)
        {
            attachment.thread.interrupt();
            System.out.println("Server shutdown unexpectedly, Broker going offline...");
            return ;
        }
        if (attachment.toWriteMessage) {
            attachment.byteBuffer.flip();
            int limits = attachment.byteBuffer.limit();
            byte[] bytes = new byte[limits];
            attachment.byteBuffer.get(bytes, 0, limits);
            String message = new String(bytes, StandardCharsets.UTF_8);
            if (attachment.id == 0) {
                attachment.id = Integer.parseInt(message);
                System.out.println("Server Responded with Id: " + attachment.id);
                attachment.toWriteMessage = false;
                attachment.channel.read(attachment.byteBuffer, attachment, this);
                return ;
            }
            else
                System.out.println("Server Response: " + message.replace((char) 1, '|'));


            attachment.byteBuffer.clear();
            message = MessageHandler.breakdownMessage(message);
            if (message.contains("bye")) {
                attachment.thread.interrupt();
                return;
            }
            try {
                System.out.println("\nMarket Response: "+ message.replace((char)1, '|'));
            } catch (Exception e) {
                e.printStackTrace();
            }
            byte[] data = message.getBytes(StandardCharsets.UTF_8);
            attachment.byteBuffer.put(data);
            attachment.byteBuffer.flip();
            attachment.toWriteMessage = false;
            attachment.channel.write(attachment.byteBuffer, attachment, this);
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
}
