import java.io.*;
import java.net.*;


public class Proxyhandlerequest {
    String endURL = "http://dummy.com";
    //maybe input random socket
    String response;

    {
        try {
            response = handlebothrequests(endURL, "proxy_host", 8081);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String handlebothrequests(String endURL, String proxyhost, int proxyport) throws Exception{

        StringBuilder response = new StringBuilder();
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyhost, proxyport));
        URl url = new URL(endURL);
        // HttpURLConnection connection = (HttpURLConnection) url.openConnection(proxy);
        // connection.setRequestMethod("GET");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openConnection(proxy).getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null){
                response.append(line).append("\n");
            }
        }
        return response.toString();
    }

    //public void handlebackrequest() throws Exception{

    //}
}
