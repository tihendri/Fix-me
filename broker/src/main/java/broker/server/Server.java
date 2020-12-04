package broker.server;

import broker.attachment.Attachment;
import broker.handlers.MessageHandler;
import broker.main.Main;

import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;

public class Server implements CompletionHandler<Integer, Attachment> {
    private static int finish = 0;
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
            if (attachment.id == 0)
            {
                attachment.id = Integer.parseInt(message);
                System.out.println("Server Responded with Id: " + attachment.id);
            }
            else
                System.out.println("Server Response: " + message.replace((char) 1, '|'));
            boolean reply = MessageHandler.processReply(message);
            if (reply) {
                if (Main.buyOrSell == 1)
                    MessageHandler.updateData(false);
                else if (Main.buyOrSell == 2)
                    MessageHandler.updateData(true);
            }

            attachment.byteBuffer.clear();
            message = MessageHandler.messageHandler();
            if (finish > 3) {
                attachment.thread.interrupt();
                return;
            }
            finish++;
            // try is unnecessary
            try {
                System.out.println("\nBroker Response: "+ message.replace((char)1, '|'));
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
