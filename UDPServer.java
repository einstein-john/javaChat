package TCP_UDP_chat;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class UDPServer {
    //gets the IP of every new tcp connection
    public static String getIP(Socket connected) {
        return String.valueOf(connected.getInetAddress());
    }
    //holds the client address after tcp connection
    static String clientIP = null;
    static Boolean exit = false;

    //ports for tcp and udp connections
    static int portTCP = 8080;
    static int portUDP = 4567;
//send port values to client.
    public static int getPortTCP() {
        return portTCP;
    }

    public static int getPortUDP() {
        return portUDP;
    }

    public static void main(String[] args) throws IOException {

        ServerSocket connect = new ServerSocket(portTCP);

        while (!exit) {
            Socket incoming = connect.accept();
            System.out.println("Waiting for connection...");

            //a print-writer to display a message when a new connection is established
            PrintWriter welcome = new PrintWriter(new OutputStreamWriter(incoming.getOutputStream()));
//TCP_UDP_chat.send this everytime you get a connection
            welcome.println("**WELCOME TO EINSTEIN's CHAT APP!!**");
            welcome.flush();

            welcome.println("Enter BYE to exit.");
            welcome.flush();

            if (incoming.isConnected()) {
                clientIP = getIP(incoming);
                System.out.println("Connected!! " + clientIP);
                welcome.close();
                incoming.close();
                UDPServer.exit = true;
            }

        // Create a DatagramSocket to listen for incoming messages
        DatagramSocket socket = new DatagramSocket(portUDP);

        // Create a buffer to hold the incoming message
        byte[] bufferS = new byte[1024];
        DatagramPacket packetS = new DatagramPacket(bufferS, bufferS.length);

        // Create separate threads for sending and receiving messages
        Thread sendThread = new Thread(new send(socket,packetS));

        Thread receiveThread = new Thread(new receive(socket,packetS));

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
//TCP_UDP_chat.send thread class
class send implements Runnable{

    Scanner sc = new Scanner(System.in);
    DatagramPacket packet;
    DatagramSocket socket;

    public send(DatagramSocket s, DatagramPacket p){
        packet = p;
        socket = s;

    }
    @Override
    public void run() {


// Enter a loop to TCP_UDP_chat.send messages

        while (!UDPServer.exit) {
            String input = sc.nextLine();

             InetAddress address = packet.getAddress();
             int port = packet.getPort();
            if (input.trim().equals("BYE")) {
                packet = new DatagramPacket("BYE".getBytes(),
                        3, address, port);
                socket.close();
                UDPServer.exit = true;
            }
            String strOUT = "RECEIVED: " + input + " \n ";

            packet = new DatagramPacket(strOUT.getBytes(StandardCharsets.UTF_8), strOUT.length(), address, port);
            try {
                socket.send(packet);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }
}

//TCP_UDP_chat.receive thread class
class receive implements Runnable{
    DatagramPacket packet;
    DatagramSocket socket;

    public receive(DatagramSocket s, DatagramPacket p){
        socket = s;
        packet = p;
    }
    @Override
    public void run() {

        // Enter a loop to listen for incoming messages
        while (!UDPServer.exit) {
            // Receive the incoming message
            try {
                socket.receive(packet);
            // Extract the message from the packet
            String message = new String(packet.getData(), 0, packet.getLength());
            System.out.println("RECEIVED: " + message + "\n");
            if (message.trim().equals("BYE")) {
                System.out.println("\n DISCONNECTED");
                socket.close();
                UDPServer.exit = true;
            }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
