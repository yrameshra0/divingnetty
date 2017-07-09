package transport;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;

public class TransportClient {
    private static volatile AtomicLong atomicLong = new AtomicLong();

    public static void main(String[] args) {
        int port = 1901;
        initiateParallelServerConnections(port);
    }

    private static void initiateParallelServerConnections(int port) {
        IntStream.range(0, Integer.MAX_VALUE)
                .parallel()
                .forEach(index -> {
                    try {
                        clientToSayServerHi(port);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }

    private static void clientToSayServerHi(int port) throws IOException {
        Socket socket = new Socket("localhost", port);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        String fromServer;
        StringBuilder response = new StringBuilder();
        while ((fromServer = bufferedReader.readLine()) != null) {
            response.append(fromServer);
        }

        System.out.println(atomicLong.incrementAndGet());

        System.out.println(Thread.currentThread().getName());
        System.out.println(response.toString());
    }
}
