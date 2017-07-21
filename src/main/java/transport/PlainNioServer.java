package transport;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class PlainNioServer {
    public static void main(String[] args) throws IOException {
        PlainNioServer plainNioServer = new PlainNioServer();
        plainNioServer.server(1901, ByteBuffer.wrap("Hi \r\n".getBytes()));
    }

    public void server(int port, ByteBuffer message) throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        ServerSocket serverSocket = serverSocketChannel.socket();
        InetSocketAddress address = new InetSocketAddress(port);
        serverSocket.bind(address);

        Selector selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        waitForClientConnection(selector, message);

    }

    private void waitForClientConnection(Selector selector, ByteBuffer message) throws IOException {
        while (true) {
            selector.select();
            Set<SelectionKey> readyKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = readyKeys.iterator();
            while (iterator.hasNext()) {

                SelectionKey selectionKey = iterator.next();
                iterator.remove();
                try {
                    acceptIncomingConnection(selector, message, selectionKey);
                    writeToClient(selectionKey);
                } catch (IOException e) {
                    e.printStackTrace();
                    selectionKey.cancel();
                }
            }
        }
    }

    private void writeToClient(SelectionKey selectionKey) throws IOException {
        if (selectionKey.isWritable()) {
            SocketChannel client = (SocketChannel) selectionKey.channel();
            ByteBuffer buffer = (ByteBuffer) selectionKey.attachment();
            while (buffer.hasRemaining()) {
                if (client.write(buffer) == 0)
                    break;
            }

            client.close();
        }
    }

    private void acceptIncomingConnection(Selector selector, ByteBuffer message, SelectionKey selectionKey) throws IOException {
        if (selectionKey.isAcceptable()) {
            ServerSocketChannel server = (ServerSocketChannel) selectionKey.channel();
            SocketChannel client = server.accept();

            client.configureBlocking(false);
            client.register(selector, SelectionKey.OP_WRITE | SelectionKey.OP_READ, message.duplicate());
            System.out.println(String.format("Accepted connection from %s", client));
        }
    }
}
