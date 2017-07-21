package bytebuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import transport.NettyOioAndNioServer;
import transport.PlainNioServer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import static io.netty.buffer.Unpooled.unreleasableBuffer;

public class CompositeBuffer {
    public static void main(String[] args) throws IOException, InterruptedException {
        //useJDKByteBufferComposition();
        useNettyByteBufComposition();
    }

    private static byte[] messageBytes(String identifier) {
        return String.format("Message Body From --> %s", identifier).getBytes();
    }

    private static byte[] headerBytes() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        return headers.toString().getBytes();
    }

    private static void useJDKByteBufferComposition() throws IOException {
        ByteBuffer header = ByteBuffer.wrap(headerBytes());
        ByteBuffer body = ByteBuffer.wrap(messageBytes("JDK"));

        ByteBuffer compositeBuffer = ByteBuffer.allocate(header.capacity() + body.capacity());
        compositeBuffer.put(header);
        compositeBuffer.put(body);
        compositeBuffer.flip();

        PlainNioServer plainNioServer = new PlainNioServer();
        plainNioServer.server(1901, compositeBuffer);
    }

    private static void useNettyByteBufComposition() throws InterruptedException {
        ByteBuf header = Unpooled.copiedBuffer(headerBytes());
        ByteBuf body = Unpooled.copiedBuffer((messageBytes("Netty")));

        CompositeByteBuf compositeByteBuf = Unpooled.compositeBuffer();
        compositeByteBuf.addComponents(true, header);
        compositeByteBuf.addComponents(true, body);

        NettyOioAndNioServer nettyOioAndNioServer = new NettyOioAndNioServer();
        nettyOioAndNioServer.serverWithNio(1901, unreleasableBuffer(compositeByteBuf));
    }

}
