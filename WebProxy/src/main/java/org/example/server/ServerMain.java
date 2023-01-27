package org.example.server;

import org.example.config.Configuration;

import java.net.ServerSocket;

public class ServerMain {
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(Configuration.port)) {
            Server server = new Server(serverSocket);
            server.listenForClientConnection();
            server.handleRequestAndResponse();
            //server.close();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}