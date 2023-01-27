package org.example.client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private final Socket socket;
    private boolean listenerRunning = true;

    public Client(Socket socket) {
        this.socket = socket;
    }

    public void handleRequestAndResponse() throws IOException {
        DataInputStream inputFromServer = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        DataOutputStream outputToServer = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        Scanner scanner = new Scanner(System.in);

        //Loop that accepts input from the client, sends the request to the server and receives response from the server
        while (listenerRunning) {
            System.out.print("Enter a request: ");
            String requestToServer = scanner.nextLine();

            outputToServer.writeUTF(requestToServer);
            outputToServer.flush();
            System.out.println("Sent request: " + requestToServer);

            String responseFromServer = inputFromServer.readUTF();
            System.out.println("Response: " + responseFromServer);
        }
    }

    public void close() throws IOException {
        listenerRunning = false;

        if (socket != null) {
            socket.close();
        }
    }
}
