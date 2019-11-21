package com.totti.socket;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;

public class SocketClient {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("127.0.0.1", 9090);
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        writer.write("Hello, World!");
        writer.flush();
        socket.shutdownOutput();
    }
}
