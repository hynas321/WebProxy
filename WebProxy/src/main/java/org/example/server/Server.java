package org.example.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private final ServerSocket serverSocket;
    private Socket connectedSocket;
    private boolean listenerRunning = true;

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void listenForClientConnection() throws IOException {
        System.out.println("Waiting for client connection...");

        while (true) {
            if (!serverSocket.isClosed()) {
                connectedSocket = serverSocket.accept();

                System.out.println("A client has connected");
                break;
            }
        }
    }

    public void handleRequestAndResponse() throws IOException {
        if (connectedSocket != null) {
            DataInputStream inputFromClient = new DataInputStream(new BufferedInputStream((connectedSocket.getInputStream())));
            DataOutputStream outputToClient = new DataOutputStream(new BufferedOutputStream(connectedSocket.getOutputStream()));

            //Loop that waits for the request from the user, sends the response to the user
            while (listenerRunning) {
                String requestFromClient = inputFromClient.readUTF();
                System.out.println("Received request: " + requestFromClient);

                String responseToClient = "Test response from the server";

                outputToClient.writeUTF(responseToClient);
                outputToClient.flush();
                System.out.println("Sent response: " + responseToClient);
            }
        }
    }

    public void close() throws IOException {
        listenerRunning = false;

        if (serverSocket != null) {
            serverSocket.close();
        }
    }
}
