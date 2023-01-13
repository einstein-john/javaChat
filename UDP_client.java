package TCP_UDP_chat;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.*;

public class UDP_client {
    public static void main(String[] args) throws IOException {
        String host = args.length > 0 ? args[0] : "localhost";
        InetAddress clientIP;

        while (true) {
            Socket incoming = new Socket(host, 8080);
            System.out.println("Waiting for connection...");

            //a print-writer to display a message when a new connection is established
            PrintWriter welcome = new PrintWriter(new OutputStreamWriter(incoming.getOutputStream()));
//TCP_UDP_chat.send this everytime you get a connection
            welcome.println("**WELCOME TO EINSTEIN's CHAT APP!!**");
            welcome.flush();

            welcome.println("Enter BYE to exit.");
            welcome.flush();

            if (incoming.isConnected()) {
                clientIP = incoming.getInetAddress();
                System.out.println("Connected!! " + clientIP);

                welcome.close();
                incoming.close();
            }

            // Create a DatagramSocket to listen for incoming messages
            DatagramSocket socket = new DatagramSocket(4567);

            // Create a buffer to hold the incoming message
            byte[] buffer = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

            // Create separate threads for sending and receiving messages
            Thread sendThread = new Thread(new send(socket,packet));


            Thread receiveThread = new Thread(new receive(socket,packet));

            // Start TCP_UDP_chat.send and TCP_UDP_chat.receive threads
            sendThread.start();
            receiveThread.start();

            try {
                receiveThread.join();
                sendThread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }


        }

    }
}
