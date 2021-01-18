package router.server;

import router.handlers.MessageHandler;
import router.table.RoutingTable;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;

public class Server implements Runnable {
    private final int currentPort;
    private static int uniqueClientId = 100000;
    public Server(int port) {
        currentPort = port;
    }
    @Override
    public void run() {
        try {
            final AsynchronousServerSocketChannel listener = AsynchronousServerSocketChannel.open().bind(new InetSocketAddress("localhost", currentPort));
            SocketAddress socketAddress = listener.getLocalAddress();
            if (currentPort == 5000) {
                System.out.printf((char)27 + "[0;32mBroker Server will listen on port: %s%n" + (char)27 + "[0m", socketAddress);
            } else if (currentPort == 5001) {
                System.out.printf((char)27 + "[0;32mMarket Server will listen on port: %s%n" + (char)27 + "[0m", socketAddress);
            }
            Attachment attachment = new Attachment();
            attachment.server = listener;
            listener.accept(attachment, new CompletionHandler<AsynchronousSocketChannel,Attachment>() {
                @Override
                public void completed(AsynchronousSocketChannel channel, Attachment attachment) {
                    SocketAddress socketAddress = null;
                    try {
                        socketAddress = channel.getRemoteAddress();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.out.printf((char)27 + "[0;32mAccepted a connection from %s%n" + (char)27 + "[0m", socketAddress);
                    // accept the next connection
                    listener.accept(attachment, this);

                    // handle this connection
                    handle(channel, attachment, socketAddress);
                }

                @Override
                public void failed(Throwable exc, Attachment attachment) {
                    System.out.println((char)27 + "[0;31mConnection Failed." + (char)27 + "[0m");
                    exc.printStackTrace();
                }
            });
            Thread.currentThread().join();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void handle(AsynchronousSocketChannel channel, Attachment attachment, SocketAddress socketAddress) {
        MessageHandler messageHandler = new MessageHandler();
        Attachment attachment1 = new Attachment();
        attachment1.server = attachment.server;
        attachment1.channel = channel;
        attachment1.channelId = uniqueClientId++;
        attachment1.byteBuffer = ByteBuffer.allocate(2048);
        attachment1.toWriteMessage = false;
        attachment1.socketAddress = socketAddress;
        byte[] data = Integer.toString(attachment1.channelId).getBytes(StandardCharsets.UTF_8);
        attachment1.messageHandler = messageHandler;
        attachment1.byteBuffer.put(data);
        attachment1.byteBuffer.flip();
        RoutingTable.addClient(attachment1);
        channel.write(attachment1.byteBuffer, attachment1, messageHandler);
    }
}
