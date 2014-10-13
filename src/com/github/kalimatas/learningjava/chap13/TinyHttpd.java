package com.github.kalimatas.learningjava.chap13;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TinyHttpd {
    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(Integer.parseInt(args[0]));
        Executor executor = Executors.newFixedThreadPool(3);

        while (true) {
            executor.execute(new TinyHttpConnetion(server.accept()));
        }
    }
}

class TinyHttpConnetion implements Runnable {
    Socket client;

    TinyHttpConnetion(Socket client) {
        this.client = client;
    }

    @Override
    public void run() {
        //System.out.println("got request!");
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream(), "8859_1"));
            OutputStream out = client.getOutputStream();
            PrintWriter pout = new PrintWriter(new OutputStreamWriter(out, "8859_1"), true);

            String request = in.readLine();
            System.out.println("request: " + request);

            Matcher get = Pattern.compile("GET /?(\\S*).*").matcher(request);
            if (get.matches()) {
                request = get.group(1);
                if (request.endsWith("/") || request.equals("")) {
                    request += "index.html";
                }

                try {
                    FileInputStream fis = new FileInputStream(request);
                    byte[] data = new byte[64 * 1024];

                    for (int read; (read = fis.read(data)) > -1; ) {
                        out.write(data, 0, read);
                    }
                    out.flush();

                } catch (FileNotFoundException e) {
                    pout.println("404 Not found");
                }
            } else {
                pout.println("400 Bad request");
            }

            client.close();
        } catch (IOException e) {
            System.out.println("I/O error " + e);
        }
    }
}
