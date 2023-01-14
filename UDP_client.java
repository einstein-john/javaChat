package TCP_UDP_chat;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class UDP_client {
    static Boolean exit = false;
    public static void main(String @NotNull [] args) throws IOException {

        String host = args.length > 0 ? args[0] : "localhost";
       //get the tcp and udp ports from the server
        int portTCP = UDPServer.getPortTCP();
        int portUDP = UDPServer.getPortUDP();

        // Create a socket to connect to the server
        Socket socket = new Socket(host, portTCP);

        // Create a buffered-reader to read messages from the server
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        // Read the welcome message from the server
        String welcome = in.readLine();
        System.out.println(welcome);
        in.close();

        // Create a scanner to read input from the user
        Scanner sc = new Scanner(System.in);

        // Create a DatagramSocket to connect to the server
        DatagramSocket ds = new DatagramSocket();
        InetAddress serverIP = InetAddress.getByName(host);
        byte[] bufferC = new byte[1024];
        DatagramPacket packetC = new DatagramPacket(bufferC, bufferC.length, serverIP, portUDP);

        // Create a thread to receive messages
        Thread receiveThread = new Thread(new receive(ds, packetC));
        receiveThread.start();

        // Enter a loop to send messages
        while (!exit) {
            String input = sc.nextLine();

            if (input.trim().equals("BYE")) {
                ds.send(packetC);
                ds.close();
                exit = true;
            }
            packetC = new DatagramPacket(input.getBytes(StandardCharsets.UTF_8),input.length(),serverIP,portUDP);
            ds.send(packetC);
        }

        // Close the socket
        socket.close();
    }
}

