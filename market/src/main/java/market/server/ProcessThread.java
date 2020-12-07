package market.server;

import market.attachment.Attachment;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.Future;

public class ProcessThread {
    public static Attachment attachment;
    public void processThread() {
        try {
            AsynchronousSocketChannel channel = AsynchronousSocketChannel.open();
            SocketAddress socketAddress = new InetSocketAddress("localhost", 5001);
            Future<Void> result = channel.connect(socketAddress);
            result.get();
            System.out.println((char)27 + "[0;32mMarket server is up and running!" + (char)27 + "[0m");
            attachment = new Attachment();
            attachment.channel = channel;
            attachment.byteBuffer = ByteBuffer.allocate(2048);
            attachment.toWriteMessage = true;
            attachment.thread = Thread.currentThread();
            Server server = new Server();
            channel.read(attachment.byteBuffer, attachment, server);
            Thread.currentThread().join();
        }
        catch(Exception e) {
            System.out.println("\n" + (char)27 + "[0;31mError: You need to run the router first in order to run the market server." + (char)27 + "[0m");
            System.out.println();
            System.exit(0);
//            e.printStackTrace();
        }
    }
}
