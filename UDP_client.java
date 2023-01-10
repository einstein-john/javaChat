package TCP_UDP_chat;

import java.io.IOException;
import java.net.Socket;

public class UDP_client {
    public static void main(String[] args) {

        String host = args.length > 0 ? args[0] : "localhost";
        int port = 8080;
        try {
            Socket sock = new Socket(host, port);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
