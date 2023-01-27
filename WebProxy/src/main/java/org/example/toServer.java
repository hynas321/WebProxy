import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;
import java.net.*;

public class toServer {
    private ServerSocket serverSocket;
    private Socket proxySocket;
    private PrintWriter out;
    private BufferedReader in;

    public void connect(int port) throws IOException{
        serverSocket = new ServerSocket(port);
        proxySocket = ServerSocket.accept();
        out = new PrintWriter(proxySocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(proxySocket.getInputStream()));
    }

    public void stop() throws IOException {
        serverSocket.close();
        proxySocket.close();
        out.close();
        in.close();
    }

}
