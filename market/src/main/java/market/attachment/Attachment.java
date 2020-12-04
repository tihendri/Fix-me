package market.attachment;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;

public class Attachment {
    public Thread thread;
    public AsynchronousSocketChannel channel;
    public ByteBuffer byteBuffer;
    public int id;
    public boolean toWriteMessage;
}
