package server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static io.netty.util.CharsetUtil.UTF_8;

@Sharable
public class EchoServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext context, Object msg) throws Exception {
        ByteBuf inputBuffer = (ByteBuf) msg;

        System.out.println(String.format("Server Received : %s", inputBuffer.toString(UTF_8)));

        context.write(inputBuffer);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext context) throws Exception {
        context.writeAndFlush(Unpooled.EMPTY_BUFFER)
                .addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext context, Throwable cause) throws Exception {
        cause.printStackTrace();
        context.close();
    }
}
