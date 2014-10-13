package com.github.kalimatas.learningjava.chap12;

import java.io.*;

class LoggerDaemon extends Thread {
    PipedReader in = new PipedReader();

    LoggerDaemon() {
        start();
    }

    public void run() {
        BufferedReader bin = new BufferedReader(in);
        String s;
        try {
            while ((s = bin.readLine()) != null) {
                System.out.println("logged: " + s);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    PrintWriter getWriter() throws IOException {
        return new PrintWriter(new PipedWriter(in));
    }
}

public class Streams {
    public static void main(String[] args) throws IOException {
        /*
        int waiting = System.in.available();
        System.out.printf("available: %d\n", waiting);
        if (waiting > 0) {
            byte[] buf = new byte[waiting];
            System.in.read(buf);
            System.out.println(buf);
        }
        */

        /*
        InputStreamReader charsIn = new InputStreamReader(System.in);
        char[] s = new char[System.in.available()];
        charsIn.read(s);
        System.out.println(s);
        */

        /*
        DataInputStream ds = new DataInputStream(System.in);
        DataOutput dos = new DataOutputStream(System.out);
        int i = 0,
            c = 42;


        try {
            i = ds.readInt();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("read int: " + i);

        try {
            dos.writeInt(c);
        } catch (IOException e) {
            e.printStackTrace();
        }
        */

        /*
        PrintWriter out = new LoggerDaemon().getWriter();
        out.println("starting app...");
        */

        if (args.length == 0) {
            System.err.println("need argument");
            System.exit(1);
        }

        File file = new File(args[0]);
        if (!file.exists() || !file.canRead()) {
            System.err.println("cannot read " + file);
            System.exit(1);
        }

        if (file.isDirectory()) {
            String[] fileList = file.list();
            for (String f : fileList) {
                System.out.println(f);
            }
        } else {
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
        }
    }
}
