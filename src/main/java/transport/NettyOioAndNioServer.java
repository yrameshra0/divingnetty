package transport;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.oio.OioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.oio.OioServerSocketChannel;

import java.net.InetSocketAddress;

import static io.netty.buffer.Unpooled.copiedBuffer;
import static io.netty.buffer.Unpooled.unreleasableBuffer;
import static io.netty.util.CharsetUtil.UTF_8;

public class NettyOioAndNioServer {
    public static void main(String[] args) throws InterruptedException {
        NettyOioAndNioServer nettyOioAndNioServer = new NettyOioAndNioServer();
        nettyOioAndNioServer.serverWithNio(1901, unreleasableBuffer(copiedBuffer("Hi \r\n", UTF_8)));
    }

    public void serverWithOio(int port, ByteBuf message) throws InterruptedException {
        server(port, new OioEventLoopGroup(), OioServerSocketChannel.class, message);
    }

    public void serverWithNio(int port, ByteBuf message) throws InterruptedException {
        server(port, new NioEventLoopGroup(), NioServerSocketChannel.class, message);
    }

    public void server(int port, EventLoopGroup eventLoopGroup, Class<? extends ServerChannel> serverChannelClazz, ByteBuf messageBuffer) throws InterruptedException {

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap
                    .group(eventLoopGroup)
                    .channel(serverChannelClazz)
                    .localAddress(new InetSocketAddress(port))
                    .childHandler(inboundChannelHandler(messageBuffer));

            ChannelFuture future = serverBootstrap.bind().sync();
            future.channel().closeFuture().sync();
        } finally {
            eventLoopGroup.shutdownGracefully();
        }
    }

    private ChannelHandler inboundChannelHandler(ByteBuf message) {
        return new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                socketChannel.pipeline().addLast(inboundChannelHandlerAdapter(message));
            }
        };
    }

    private ChannelHandler inboundChannelHandlerAdapter(ByteBuf message) {
        return new ChannelInboundHandlerAdapter() {

            @Override
            public void channelActive(ChannelHandlerContext ctx) throws Exception {
                ctx.writeAndFlush(message).addListener(ChannelFutureListener.CLOSE);
            }
        };
    }
}
