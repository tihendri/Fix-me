package router.table;

import router.server.Attachment;
import router.server.WhoIsResponsible;

public class Return {
    private static final WhoIsResponsible WHO_IS_RESPONSIBLE = WhoIsResponsible.SOURCE;

    public void returnMessage(Attachment attachment, WhoIsResponsible responsible)
    {
        if (responsible != WHO_IS_RESPONSIBLE)
            return ;
        attachment.toWriteMessage = false;
        attachment.channel.write(attachment.byteBuffer, attachment, attachment.messageHandler);
    }
}
