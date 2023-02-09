package org.example.proxy;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.nio.charset.StandardCharsets;

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

                break;
            }
        }
    }

    public void run() {
        try {
            if (clientSocket != null) {
                InputStream input = clientSocket.getInputStream();
                OutputStream output = clientSocket.getOutputStream();

                byte[] requestData = new byte[4096];
                int bytesRead = input.read(requestData);
                if (bytesRead == -1) {
                    return;
                }

                String request = new String(requestData, 0, bytesRead, StandardCharsets.UTF_8);

                String[] tokens = request.split(" ");
                String method = tokens[0];  //GET
                String url = tokens[1]; //http://zebroid.ida.liu.se/fakenews/test1.txt
                String httpVersion = "HTTP/1.0";

                URI uri = new URI(url);
                String scheme = uri.getScheme(); //HTTP
                String host = uri.getHost(); //zebroid.ida.liu.se
                int port = 80;  //HTTP port

                if (!scheme.equals("http")) {
                    return;
                }

                System.out.println(request);

                try (Socket server = new Socket(host, port)) {
                    OutputStream serverOutput = server.getOutputStream();
                    String requestLine = method + " " + url + " " + httpVersion + "\r\n\r\n";

                    serverOutput.write(requestLine.getBytes());

                    InputStream serverInput = server.getInputStream();
                    ByteArrayOutputStream response = new ByteArrayOutputStream();
                    byte[] buffer = new byte[4096];
                    int bytesReceived;

                    while ((bytesReceived = serverInput.read(buffer)) != -1) {
                        response.write(buffer, 0, bytesReceived);
                    }

                    String responseString = new String(response.toByteArray(), StandardCharsets.UTF_8);
                    String[] lines = responseString.split("\r\n");

                    for (String line : lines) {
                        if (line.contains(Keywords.SMILEY) && !line.contains(Keywords.IMG_TAG)) {
                            line = line.replaceAll("\\b" + Keywords.SMILEY + "\\b", Keywords.TROLLY);
                        }
                        if (line.contains(Keywords.STOCKHOLM) && !line.contains(Keywords.IMG_TAG)) {
                            line = line.replaceAll("\\b" + Keywords.STOCKHOLM + "\\b", Keywords.LINKOPING);
                        }
                        System.out.println(line);
                    }

                    output.write(response.toByteArray());
                }

                clientSocket.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
