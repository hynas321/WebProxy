import java.net.*;
import java.io.*;

public class toClient {
    private Socket proxySocket;
    private PrintWriter out;
    private BufferedReader in;

    public void connectconnection(String ip, int port) throws IOException {
        proxySocket = new Socket(ip, port);
        out = new PrintWriter(proxySocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(proxySocket.getInputStream()));
    }

    public void stop() throws IOException {
        proxySocket.close();
        out.close();
        in.close();
    }



}
