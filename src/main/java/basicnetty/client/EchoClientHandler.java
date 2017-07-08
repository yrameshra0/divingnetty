package basicnetty.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import static io.netty.util.CharsetUtil.UTF_8;

@Sharable
public class EchoClientHandler extends SimpleChannelInboundHandler<ByteBuf> {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(Unpooled.copiedBuffer("Netty Rocks 2!!!", UTF_8));
    }

    protected void channelRead0(ChannelHandlerContext context, ByteBuf readBuffer) throws Exception {
        System.out.println(String.format("Client Received : %s", readBuffer.toString(UTF_8)));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext context, Throwable cause) throws Exception {
        cause.printStackTrace();

        context.close();
    }
}
