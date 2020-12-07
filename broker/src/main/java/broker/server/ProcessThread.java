package broker.server;

import broker.attachment.Attachment;

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
            SocketAddress socketAddress = new InetSocketAddress("localhost", 5000);
            Future<Void> result = channel.connect(socketAddress);
            result.get();
            System.out.println((char)27 + "[0;32mBroker server is up and running!" + (char)27 + "[0m");
            attachment = new Attachment();
            attachment.channel = channel;
            attachment.byteBuffer = ByteBuffer.allocate(2048);
            attachment.toWriteMessage = true;
            attachment.thread = Thread.currentThread();
            Server server = new Server();
            channel.read(attachment.byteBuffer, attachment, server);
        }
        catch(Exception e) {
            System.out.println("\n" + (char)27 + "[0;31mError: You need to run the router and market first in order to run the broker server." + (char)27 + "[0m");
            System.out.println();
            System.exit(0);
            e.printStackTrace();
        }
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            System.out.println("\n" + (char)27 + "[0;32mComplete." + (char)27 + "[0m");
        }
    }
}
