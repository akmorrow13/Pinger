import java.net.*;
import java.nio.ByteBuffer;
import java.io.*;


public class Server {
	
	private int port;
	
	public Server(int port){
		this.port = port;
	}
	
	public void listenClients() throws IOException{
		
		DatagramSocket socket = null;
		
		try {
			socket = new DatagramSocket(port);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		byte[] clientBuffer = new byte[12];
		DatagramPacket packet = new DatagramPacket(clientBuffer, clientBuffer.length);
		
		while(true){
			
			socket.receive(packet);
			
			printInfo(packet);
			sendMessageBackClient(socket, packet);		
			
		}

	}

	private void printInfo(DatagramPacket packet){
		
		// This method print in the screen information about the received packet

		long timePackedReceived = System.currentTimeMillis();
		String clientAddress = packet.getAddress().getHostAddress();
		byte messageClient[] = packet.getData();
		
		ByteBuffer bb = ByteBuffer.wrap(messageClient);
		int seq = bb.getInt(); // Actually, it is necessary to get the first 4 bytes from the array
		
		System.out.println("time="+ timePackedReceived +" from="+ clientAddress +" seq=" + seq);
	
	}
	
	
	private void sendMessageBackClient(DatagramSocket socket, DatagramPacket packet) throws IOException{
		
		// This method send back the same packet received from this client
		
		byte[] messageToClient = packet.getData();
	    DatagramPacket messageBack = new DatagramPacket(messageToClient, messageToClient.length, packet.getAddress(), packet.getPort());
	    socket.send(messageBack);
	}

}
