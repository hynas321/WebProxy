package org.example.proxy;

import org.example.config.Configuration;

import java.net.ServerSocket;

public class WebProxyMain {
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(Configuration.port)) {
            WebProxy webProxy = new WebProxy(serverSocket);

            while(true) {
                webProxy.awaitClientConnection();
                webProxy.run();
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}