package org.example.proxy;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

public class WebProxy {
    private final ServerSocket serverSocket;
    private Socket clientSocket;
    private InputStream clientInputStream;
    private InputStream serverInputStream;
    private OutputStream clientOutputStream;
    private OutputStream serverOutputStream;
    private ByteArrayOutputStream byteResponseOutputStream;

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
                clientInputStream = clientSocket.getInputStream();
                clientOutputStream = clientSocket.getOutputStream();

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
                    serverOutputStream = server.getOutputStream();

                    String requestLine = method + " " + url + " " + httpVersion + "\r\n\r\n";

                    serverOutputStream.write(requestLine.getBytes());

                    serverInputStream = server.getInputStream();

                    byteResponseOutputStream = new ByteArrayOutputStream();

                    byte[] buffer = new byte[4096];
                    int receivedBytesCount;

                    while ((receivedBytesCount = serverInputStream.read(buffer)) != -1) {
                        byteResponseOutputStream.write(buffer, 0, receivedBytesCount);
                    }

                    byte[] modifiedResponseBytes = modifyResponse(byteResponseOutputStream.toByteArray());

                    byteResponseOutputStream.reset();
                    byteResponseOutputStream.write(modifiedResponseBytes);

                    int responseLength = byteResponseOutputStream.toByteArray().length;

                    System.out.println(byteResponseOutputStream);

                    for (byte b : byteResponseOutputStream.toByteArray()) {
                        System.out.print(Integer.toHexString(b) + " ");
                    }

                    clientOutputStream.write(modifiedResponseBytes, 0, responseLength);
                    clientOutputStream.flush();
                }

                close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void close() throws IOException {
        clientInputStream.close();
        serverInputStream.close();
        clientOutputStream.close();
        serverOutputStream.close();
        byteResponseOutputStream.close();
        clientSocket.close();
    }

    private byte[] modifyResponse(byte[] originalResponse) {
        byte[] modifiedResponse1 = replaceBytes(originalResponse, Keywords.SMILEY, Keywords.TROLLY);
        byte[] modifiedResponse2 = replaceBytes(modifiedResponse1, Keywords.STOCKHOLM, Keywords.LINKOPING);
        byte[] modifiedResponse3 = replaceBytes(modifiedResponse2, Keywords.SMILEY_IMG_JPG, Keywords.TROLLY_IMG_JPG);

        return replaceBytes(modifiedResponse3, Keywords.LINKOPING_IMG_JPG, Keywords.STOCKHOLM_IMG_JPG);
    }

    private static byte[] replaceBytes(byte[] originalBytes, byte[] pattern, byte[] replacement) {
        ArrayList<Byte> newBytes = new ArrayList<>();

        for (int i = 0; i < originalBytes.length; i++) {
            if (i <= originalBytes.length - pattern.length &&
                Arrays.equals(Arrays.copyOfRange(originalBytes, i, i + pattern.length), pattern)) {

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
