import foo.handlers.EchoHandler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.Test;
import static org.junit.Assert.*;

public class HandlerTest {
    @Test
    public void testRead() {
        ByteBuf buf = Unpooled.copiedBuffer("good day".getBytes());
        ByteBuf input = buf.duplicate();

        EchoHandler handler = new EchoHandler();
        EmbeddedChannel embeddedChannel = new EmbeddedChannel(handler);

//        assertTrue(embeddedChannel.writeInbound(input.retain()));
        embeddedChannel.writeInbound(input.retain());
        embeddedChannel.finish();
        ByteBuf read = embeddedChannel.readInbound();
        System.out.println("res: " + read);

    }
}
