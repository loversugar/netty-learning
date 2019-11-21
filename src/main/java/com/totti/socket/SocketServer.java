package com.totti.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketServer {
    public static void main(String[] args) throws IOException {

        //address family determines the format of address structure to be used on sockets APIs.
        //socket type
        //socket protocol
        ServerSocket serverSocket = new ServerSocket(9090);
        while (true) {
            System.out.println("now blocking");
            Socket socket = serverSocket.accept();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String str = bufferedReader.readLine();
            System.out.println(str);
        }

    }
}
