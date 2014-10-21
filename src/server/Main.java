package server;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
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
    private CharsetDecoder asciiDecoder = Charset.forName("US-ASCII").newDecoder();

    private List<SocketChannel> clients = new LinkedList<>();
    private ServerSocketChannel ssc;
    private Selector readSelector;

    /**
     * Client part
     */
    void client() {
        LOGGER.info("starting client");

        try {
            csc = SocketChannel.open(new InetSocketAddress(InetAddress.getLoopbackAddress(), PORT));
            csc.configureBlocking(false);

            // send messages to server
            (new ClientWriter(csc)).start();

            while (true) {
                Packet packet = readPacket(csc);
                if (packet != null) {
                    System.out.println(packet.get());
                    System.out.println(packet.get());
                }

                Thread.sleep(200);
            }

        } catch (IOException e) {
            LOGGER.severe("cannot connect to server");
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private Packet readPacket(SocketChannel channel) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            int bytesRead = channel.read(buffer);

            while (bytesRead != 0) {
                buffer.flip();
                baos.write(buffer.array(), 0, bytesRead);
                buffer.clear();
                bytesRead = channel.read(buffer);
            }

            if (baos.size() > 0) {
                return Packet.decode(baos.toByteArray());
            }
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }

        return null;
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
                clients.add(clientChannel);
                clientChannel.configureBlocking(false);
                //SelectionKey readKey = clientChannel.register(readSelector, SelectionKey.OP_READ);

                // send broadcast
                sendBroadcastingMessage("New client: " + clientChannel.socket().getInetAddress() + "\n");

                // send welcome to the new client
                sendMessage(clientChannel, "Welcome to server! There are " + clients.size() + " online.\n");

            }
        } catch (IOException e) {
            LOGGER.warning("error while accept()");
            e.printStackTrace();
        } catch (Exception e) {
            LOGGER.warning("error in acceptNewConnections");
            e.printStackTrace();
        }
    }

    private void sendMessage(SocketChannel channel, String message) {
        channelWrite(channel, message);
    }

    private void sendBroadcastingMessage(String message) {
        for (SocketChannel channel : clients) {
            channelWrite(channel, message);
        }
    }

    private void channelWrite(SocketChannel channel, String message) {
        try {
            Packet packet = new Packet();
            packet.append(message);
            channel.write(ByteBuffer.wrap(Packet.encode(packet)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readMessages() {

    }
}

class ClientWriter extends Thread
{
    private SocketChannel channel;
    private boolean running = true;

    ClientWriter(SocketChannel channel) {
        this.channel = channel;
    }

    public void run() {
        int elapsed = 0, step = 200;
        while (running) {
            try {
                Thread.sleep(step);
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }

            elapsed += step;

            // each two seconds
            if (elapsed >= 4000) {
                try {
                    String message = "message from client: " + channel.socket().getInetAddress();
                    Packet packet = new Packet();
                    packet.append(message);
                    channel.write(ByteBuffer.wrap(Packet.encode(packet)));

                    elapsed = 0;
                } catch (IOException e) {
                    e.printStackTrace();
                    shutdown();
                }

            }
        }
    }

    public void shutdown() {
        running = false;
        interrupt();
    }
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
