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
                BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
                OutputStream output = clientSocket.getOutputStream();

                String request = input.readLine();

                if (request == null) {
                    System.err.println("Request is null");
                    return;
                }

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

                    BufferedReader serverInput = new BufferedReader(new InputStreamReader(server.getInputStream(), StandardCharsets.UTF_8));
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
