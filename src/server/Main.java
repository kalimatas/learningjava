package server;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class Main {
    public static void main(String... args) throws IOException, InterruptedException {
        final GameServer gs = new GameServer();

        (new Thread() {
            public void run() {
                try {
                    gs.server();
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        gs.client();
    }
}

class GameServer {
    void client() throws IOException {
        System.out.println("start");

        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        socketChannel.connect(new InetSocketAddress("127.0.0.1", 9090));
        while (!socketChannel.finishConnect()) {}
        //socket = new Socket("127.0.0.1", 9090);

        try {
            /*
            PrintWriter out = new PrintWriter(socket.getOutputStream());
            out.print("GET / HTTP/1.1\n");
            out.print("Host: main.local\n\n");
            out.flush();
            */

            InputStreamReader ins = new InputStreamReader(socketChannel.socket().getInputStream());
            System.out.println("read: " + new BufferedReader(ins).readLine());

        } finally {
            socketChannel.close();
        }
    }

    void server() throws IOException, InterruptedException {
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);
        ssc.bind(new InetSocketAddress(9090));
        //ServerSocket ss = new ServerSocket(9090);

        boolean isDone = false;
        while (!isDone) {
            SocketChannel socketChannel = ssc.accept();
            if (socketChannel != null) {
                System.out.println("got connection!");

                PrintWriter out = new PrintWriter(socketChannel.socket().getOutputStream());
                out.println("hello from server");
                out.flush();

                Thread.sleep(500);
                socketChannel.close();
                isDone = true;
            }
        }
    }
}
