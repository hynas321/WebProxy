package org.example.proxy;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.ArrayList;
import java.util.Arrays;

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
                InputStream clientInputStream = clientSocket.getInputStream();
                OutputStream clientOutputStream = clientSocket.getOutputStream();

                byte[] requestData = new byte[4096];
                int bytesRead = clientInputStream.read(requestData);

                if (bytesRead == -1) {
                    return;
                }

                String request = new String(requestData, 0, bytesRead, StandardCharsets.UTF_8);
                String[] tokens = request.split(" ");
                String method = tokens[0];  //GET
                String url = tokens[1]; //Sample url: http://zebroid.ida.liu.se/fakenews/test1.txt
                String httpVersion = "HTTP/1.0";

                URI uri = new URI(url);
                String scheme = uri.getScheme(); //HTTP
                String host = uri.getHost(); //zebroid.ida.liu.se
                int port = 80;  //HTTP port

                if (!scheme.equals("http") || !host.equals("zebroid.ida.liu.se")) {
                    return;
                }

                System.out.println(request);

                try (Socket server = new Socket(host, port)) {
                    OutputStream serverOutput = server.getOutputStream();
                    String requestLine = method + " " + url + " " + httpVersion + "\r\n\r\n";

                    serverOutput.write(requestLine.getBytes());

                    InputStream serverInputStream = server.getInputStream();
                    ByteArrayOutputStream byteResponseOutputStream = new ByteArrayOutputStream();

                    byte[] buffer = new byte[4096];
                    int receivedBytesCount;

                    while ((receivedBytesCount = serverInputStream.read(buffer)) != -1) {
                        byteResponseOutputStream.write(buffer, 0, receivedBytesCount);
                    }

                    byte[] modifiedResponseBytes = modifyResponse(byteResponseOutputStream.toByteArray());

                    clientOutputStream.write(modifiedResponseBytes);
                }

                clientSocket.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    public byte[] modifyResponse(byte[] originalResponse) {
        byte[] modifiedResponse1 = replaceBytes(originalResponse, Keywords.SMILEY, Keywords.TROLLY);
        byte[] modifiedResponse2 = replaceBytes(modifiedResponse1, Keywords.STOCKHOLM, Keywords.LINKOPING);
        byte[] modifiedResponse3 = replaceBytes(modifiedResponse2, Keywords.SMILEY_IMG_JPG, Keywords.TROLLY_IMG_JPG);

        return modifiedResponse3;
    }

    public static byte[] replaceBytes(byte[] originalBytes, byte[] pattern, byte[] replacement) {
        int byteSequenceLength = originalBytes.length;

        ArrayList<Byte> newBytes = new ArrayList<>();
        int j = 0;

        for (int i = 0; i < originalBytes.length; i++) {
            if (i <= originalBytes.length - pattern.length && Arrays.equals(Arrays.copyOfRange(originalBytes, i, i + pattern.length), pattern)) {
                for (byte b : replacement) {
                    newBytes.add(b);
                }
                i += pattern.length - 1;
            } else {
                newBytes.add(originalBytes[i]);
            }
        }

        byte[] result = new byte[newBytes.size()];

        for (int i = 0; i < newBytes.size(); i++) {
            result[i] = newBytes.get(i);
        }

        return result;
    }
}
