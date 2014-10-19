package server;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

public class Main {
    public static void main(String... args) throws IOException, InterruptedException, ClassNotFoundException {
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

class Packet implements Serializable {
    private LinkedList data = new LinkedList();

    @SuppressWarnings("unchecked")
    public void append(Object obj) {
        data.add(obj);
    }

    public Object get() {
        return data.removeFirst();
    }

    public static byte[] encode(Packet packet) throws IOException {
        try (
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos)
        ) {
            oos.writeObject(packet);
            return baos.toByteArray();
        }
    }

    public static Packet decode(byte[] encodedPacket) throws IOException, ClassNotFoundException {
        try (ObjectInputStream oi = new ObjectInputStream(new ByteArrayInputStream(encodedPacket))) {
            return (Packet) oi.readObject();
        }
    }
}

class GameServer {
    void client() throws IOException, ClassNotFoundException {
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        socketChannel.connect(new InetSocketAddress("127.0.0.1", 9090));
        if (socketChannel.isConnectionPending()) {
            socketChannel.finishConnect();
        }
        //while (!socketChannel.finishConnect()) {}

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            ByteBuffer buf = ByteBuffer.allocate(2048);
            int bytesRead;
            while ((bytesRead = socketChannel.read(buf)) != -1) {
                buf.flip();
                baos.write(buf.array(), 0, bytesRead);
                buf.clear();
            }

            Packet packet = Packet.decode(baos.toByteArray());
            System.out.println("got: " + packet.get());
            System.out.println("got: " + packet.get());

        } finally {
            socketChannel.close();
        }
    }

    void server() throws IOException, InterruptedException {
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);
        ssc.bind(new InetSocketAddress(9090));

        while (true) {
            SocketChannel socketChannel = ssc.accept();
            if (socketChannel != null) {
                System.out.println("got connection!");

                Packet packet = new Packet();
                packet.append("send from server");
                packet.append(42);

                socketChannel.write(ByteBuffer.wrap(Packet.encode(packet)));

                Thread.sleep(500);
                socketChannel.close();

                break;
            }
            System.out.println("no client :(");
        }

        /*
        Selector selector = Selector.open();
        ssc.register(selector, SelectionKey.OP_ACCEPT);

        while (true) {
            selector.select();
            Set<SelectionKey> keys = selector.keys();
            Iterator<SelectionKey> i = keys.iterator();

            while (i.hasNext()) {
                SelectionKey key = i.next();
                i.remove();

                if (key.isAcceptable()) {
                    SocketChannel client = ssc.accept();
                    client.configureBlocking(false);
                    client.register(selector, SelectionKey.OP_READ);
                    continue;
                }

                if (key.isReadable()) {

                }
            }
        }
        */
    }
}
