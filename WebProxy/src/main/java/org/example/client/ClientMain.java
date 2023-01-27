package org.example.client;

import org.example.config.Configuration;

import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientMain {
    public static void main(String[] args) throws UnknownHostException {
        InetAddress address = InetAddress.getLocalHost();
        String ipAddress = address.getHostAddress();

        try (Socket socket = new Socket(ipAddress, Configuration.port)) {
            Client client = new Client(socket);
            client.handleRequestAndResponse();
            //client.close();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}