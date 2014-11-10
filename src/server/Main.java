package server;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.*;
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

    public int size() {
        return data.size();
    }

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

    private static final int PACKET_SIZE_LENGTH = 4;

    private SocketChannel csc;
    private boolean clientRunning = true;
    private ByteBuffer clientReadBuffer;
    private ByteBuffer packetSizeReadBuffer = ByteBuffer.allocate(PACKET_SIZE_LENGTH);
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

            readSelector = Selector.open();
            csc.register(readSelector, SelectionKey.OP_READ);

            // Send messages to server
            //(new Timer()).schedule(new ClientScheduleWriter(csc), 0, 4000);

            while (clientRunning) {
                readSelector.selectNow();

                Set<SelectionKey> readKeys = readSelector.selectedKeys();
                Iterator<SelectionKey> it = readKeys.iterator();

                while (it.hasNext()) {
                    LOGGER.info("got next read client");
                    SelectionKey key = it.next();
                    it.remove();

                    SocketChannel channel = (SocketChannel) key.channel();

                    Packet packet = readPacket(channel);
                    //String packet = readPacket(channel);
                    if (packet != null && packet.size() > 0) {
                        LOGGER.info((String) packet.get());
                        //LOGGER.info(packet);
                    }
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
        LOGGER.info("reading packet");

        try {
            int bytesRead;

            clientReadBuffer = ByteBuffer.allocate(3000);
            bytesRead = channel.read(clientReadBuffer);
            System.out.println("read bytes for packet: " + bytesRead);

            if (true) return null;

            // Read packet size
            packetSizeReadBuffer.clear();
            bytesRead = channel.read(packetSizeReadBuffer);
            System.out.println("bytes read: " + bytesRead);

            if (bytesRead == -1) {
                channel.close();
                clientRunning = false;
                LOGGER.info("shutting down...");
                return null;
            }

            if (bytesRead == 0) return null;

            /*
            for (byte b : packetSizeReadBuffer.array()) {
                System.out.format("0x%02x ", b);
            }
            */

            packetSizeReadBuffer.flip();
            int packetSize = packetSizeReadBuffer.getInt();
            System.out.println("got int: " + packetSize);

            // Read packet
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            clientReadBuffer = ByteBuffer.allocate(packetSize);
            bytesRead = channel.read(clientReadBuffer);
            System.out.println("read bytes for packet: " + bytesRead);

            if (bytesRead == -1) {
                channel.close();
                clientRunning = false;
                LOGGER.info("shutting down...");
                return null;
            }

            if (bytesRead == 0) return null;

            // clientReadBuffer.position()
            //while (bytesRead != 0) {
                clientReadBuffer.flip();
                baos.write(clientReadBuffer.array(), 0, bytesRead);
                //bytesRead = channel.read(clientReadBuffer);
                clientReadBuffer.clear();
            //}

            if (baos.size() > 0) {
                //System.out.println("read: " + baos.size() + "\n");
                return Packet.decode(baos.toByteArray());
                //return asciiDecoder.decode(ByteBuffer.wrap(baos.toByteArray())).toString();
                //return "test message";
            }

        } catch (IOException | ClassNotFoundException e) {
        //} catch (IOException e) {
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
                LOGGER.info("got connection from " + clientChannel.socket().getInetAddress() + "\n");

                // add client to the list
                clients.add(clientChannel);
                clientChannel.configureBlocking(false);
                //SelectionKey readKey = clientChannel.register(readSelector, SelectionKey.OP_READ);

                // send broadcast
                //sendBroadcastingMessage("New client: " + clientChannel.socket().getInetAddress() + "\n");

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
            LOGGER.info("writing message: " + message);
            Packet packet = new Packet();
            packet.append(message);
            byte[] encodedPacket = Packet.encode(packet);
            int packetSize = encodedPacket.length;
            LOGGER.info("packet size sent: " + packetSize);

            ByteBuffer packetSizeBuffer = ByteBuffer.allocate(PACKET_SIZE_LENGTH).putInt(packetSize);
            packetSizeBuffer.flip();
            channel.write(packetSizeBuffer);

            ByteBuffer packetBuffer = ByteBuffer.wrap(encodedPacket);
            packetBuffer.flip();
            packetBuffer.rewind();
            channel.write(packetBuffer);
            //int written = channel.write(ByteBuffer.wrap(Packet.encode(packet)));
            //int written = channel.write(ByteBuffer.wrap(message.getBytes()));
            //LOGGER.info("written: " + written);

            Thread.sleep(10);

            /*
            ByteBuffer writeBuffer = ByteBuffer.allocateDirect(1024);
            writeBuffer.clear();
            writeBuffer.put(Packet.encode(packet));
            writeBuffer.flip();

            int bytesWritten = 0;
            int toWrite = writeBuffer.remaining();

            try {
                while (bytesWritten != toWrite) {
                    bytesWritten += channel.write(writeBuffer);
                }
            } catch (Exception e) {
                LOGGER.warning("cannot write to channel");
                e.printStackTrace();
            }

            writeBuffer.rewind();
            */
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void readMessages() {

    }
}

class ClientScheduleWriter extends TimerTask {
    private SocketChannel channel;

    ClientScheduleWriter(SocketChannel channel) {
        this.channel = channel;
    }

    @Override
    public void run() {
        try {
            System.out.println("writing message to server\n");
            String message = "message from client: " + channel.socket().getInetAddress();
            //Packet packet = new Packet();
            //packet.append(message);
            //channel.write(ByteBuffer.wrap(Packet.encode(packet)));
            channel.write(ByteBuffer.wrap(message.getBytes()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
