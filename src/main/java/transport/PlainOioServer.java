package transport;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;

public class PlainOioServer {

    public static void main(String[] args) throws IOException {
        PlainOioServer plainOioServer = new PlainOioServer();
        plainOioServer.serve(1901);
    }

    @SuppressWarnings("InfiniteLoopStatement")
    public void serve(int port) throws IOException {
        final ServerSocket serverSocket = new ServerSocket(port);
        while (true) {
            final Socket clientSocket = serverSocket.accept();
            System.out.println(String.format("Accepted connection from %s", serverSocket));
            new Thread(() -> writeGreetingsToClient(clientSocket)).start();
        }
    }

    private void writeGreetingsToClient(Socket clientSocket) {
        OutputStream outputStream = null;
        try {
            outputStream = clientSocket.getOutputStream();
            outputStream.write("Hi \r\n".getBytes(Charset.forName("UTF-8")));
            outputStream.flush();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            terminateCommunication(clientSocket, outputStream);
        }
    }

    private void terminateCommunication(Socket clientSocket, OutputStream outputStream) {
        try {
            outputStream.close();

            clientSocket.close();
        } catch (Exception e) {
            throw new RuntimeException("Closing and flushing connections caused issue", e);
        }
    }
}
