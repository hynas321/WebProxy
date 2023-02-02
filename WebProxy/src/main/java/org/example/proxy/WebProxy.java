package org.example.proxy;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;

public class WebProxy {
    private final ServerSocket serverSocket;
    private Socket clientSocket;

    public WebProxy(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
        System.out.println("Server is running on port " + serverSocket.getLocalPort());
    }

    public void awaitClientConnection() throws IOException {
        while (true) {
            if (!serverSocket.isClosed()) {
                clientSocket = serverSocket.accept();

                System.out.println("Client has connected");
                break;
            }
        }
    }

    public void run() {
        try {
            if (clientSocket != null) {
                BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                OutputStream output = clientSocket.getOutputStream();

                String request = input.readLine();
                System.out.println(request);

                String[] tokens = request.split(" ");
                String method = tokens[0];
                String url = tokens[1];

                URI uri = new URI(url);
                String scheme = uri.getScheme();
                String host = uri.getHost();
                int port = 80;  //HTTP port

                if (!scheme.equals("http")) {
                    System.err.println("Unsupported scheme: " + scheme);
                    return;
                }

                try (Socket server = new Socket(host, port)) {
                    OutputStream serverOutput = server.getOutputStream();
                    String requestLine = method + " " + url + " HTTP/1.0\r\n\r\n";

                    serverOutput.write(requestLine.getBytes());

                    BufferedReader serverInput = new BufferedReader(new InputStreamReader(server.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;

                    while ((line = serverInput.readLine()) != null) {
                        if (line.contains(Keywords.Smiley)) {
                            line = line.replaceAll("\\b" + Keywords.Smiley + "\\b", Keywords.Trolly);
                        }
                        if (line.contains(Keywords.Stockholm)) {
                            line = line.replaceAll("\\b" + Keywords.Stockholm + "\\b", Keywords.Linkoping);
                        }

                        response.append(line + "\r\n");
                        System.out.println(line);
                    }

                    output.write(response.toString().getBytes());
                }

                clientSocket.close();
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
