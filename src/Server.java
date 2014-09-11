import java.net.*;
import java.nio.ByteBuffer;
import java.io.*;


public class Server {
	
	// Attibutes
	
	private int port;
	
	// Constructors
	
	public Server(int port){
		this.port = port;
	}
	
	// Methods
	
	public void listenClients() throws IOException{
		
		DatagramSocket socket = null;
		
		// Creating a new UDP socket
		
		try {
			socket = new DatagramSocket(port);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Creating a buffer and a UDP packet/ datagram
		
		byte[] clientBuffer = new byte[12];
		DatagramPacket packet = new DatagramPacket(clientBuffer, clientBuffer.length);
		
		// The server keep listening and receiving new connections from clients
		
		while(true){
			
			socket.receive(packet);
			
			// Printing the information of the packet 
			printInfo(packet);
			// Sending back the message to the client
			sendMessageBackClient(socket, packet);		
			
		}

	}
	
	// This method print in the screen information about the received packet

	private void printInfo(DatagramPacket packet){
		
		
		long timePackedReceived = System.currentTimeMillis();
		String clientAddress = packet.getAddress().getHostAddress();
		byte messageClient[] = packet.getData();
		
		ByteBuffer bb = ByteBuffer.wrap(messageClient);
		int seq = bb.getInt(); // Actually, it is necessary to get only the first 4 bytes from the array
		
		System.out.println("time="+ timePackedReceived +" from="+ clientAddress +" seq=" + seq);
	
	}
	
	// This method send back the same packet to the client
	
	private void sendMessageBackClient(DatagramSocket socket, DatagramPacket packet) throws IOException{
		
		byte[] messageToClient = packet.getData();
	    DatagramPacket messageBack = new DatagramPacket(messageToClient, messageToClient.length, packet.getAddress(), packet.getPort());
	    socket.send(messageBack);
	}

}
