package router.server;

import router.handlers.MessageHandler;

import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;

public class Attachment {
    public AsynchronousServerSocketChannel server;
    public AsynchronousSocketChannel channel;
    public int channelId;
    public ByteBuffer byteBuffer;
    public SocketAddress socketAddress;
    public String[] message;
    public MessageHandler messageHandler;
    public boolean toWriteMessage;
}
