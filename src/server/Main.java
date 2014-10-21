package server;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

public class Main {
    public static void main(String... args) {
        int mode = 1;
        if (args.length == 1) {
            try {
                mode = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                mode = 1;
            }
        }

        final GameServer gs = new GameServer();

        switch (mode) {
            case 1:
                gs.client();
                break;
            case 2:
                gs.server();
                break;
            default:
                throw new RuntimeException("unknown mode");
        }
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
    private static final int PORT = 4444;
    private static final Logger LOGGER = Logger.getLogger(GameServer.class.getName());

    private SocketChannel csc;

    private List<SocketChannel> clients = new LinkedList<>();
    private ServerSocketChannel ssc;
    private Selector readSelector;

    /**
     * Client part
     */
    void client() {
        LOGGER.info("starting client");

        try {
            csc = SocketChannel.open();
            csc.configureBlocking(false);
            csc.connect(new InetSocketAddress(InetAddress.getLoopbackAddress(), PORT));
            while (!csc.finishConnect()) {}
        } catch (IOException e) {
            LOGGER.severe("cannot connect to server");
            e.printStackTrace();
        }
    }

    /**
     * Server part
     */
    void server() {
        LOGGER.info("starting server");

        try {
            ssc = ServerSocketChannel.open();
            ssc.configureBlocking(false);
            ssc.bind(new InetSocketAddress(InetAddress.getLoopbackAddress(), PORT));

            readSelector = Selector.open();
        } catch (IOException e) {
            LOGGER.severe("cannot init server");
            e.printStackTrace();
        }

        LOGGER.info("running server");
        while (true) {
            acceptNewConnections();

            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void acceptNewConnections() {
        SocketChannel clientChannel;
        try {
            while ((clientChannel = ssc.accept()) != null) {
                LOGGER.info("got connection from " + clientChannel.socket().getInetAddress());

                // add client to the list


                // send broadcast
                // send reply to the new client
            }
        } catch (IOException e) {
            LOGGER.warning("error while accept()");
            e.printStackTrace();
        } catch (Exception e) {
            LOGGER.warning("error in acceptNewConnections");
            e.printStackTrace();
        }
    }

    private void readMessages() {

    }
}

/*
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
 */

/*
        Packet packet = new Packet();
                packet.append("send from server");
                packet.append(42);

                socketChannel.write(ByteBuffer.wrap(Packet.encode(packet)));
        */

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
