import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;



public class Client {
	
	// Attributes

	private int localPort;
	private String remoteHost;
	private int remotePort;
	private int packetCount;
	private byte message[];
	
	// Constructors
	
	public Client(int localPort, String remoteHost, int remotePort, int packetCount) {
		this.localPort = localPort;
		this.remoteHost = remoteHost;
		this.remotePort = remotePort;
		this.packetCount = packetCount;
		
	}
	
	// Methods
	
	public void sendPacket() throws InterruptedException, IOException {
		
		DatagramSocket clientSocket = null;
		byte[][] packetsSent = new byte[packetCount][12];
		
		int sequence = 1; // In the assignment, the fist packet is the number 1
		
		try {
			clientSocket = new DatagramSocket(localPort);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		
		// Starting the thread which receive the packets from the server
		
		ClientReceiver clientReceiver = new ClientReceiver(packetsSent, clientSocket);
		Thread thread = new Thread(clientReceiver);
		thread.start();
		
		
		for (int i = 0; i < packetCount; i++) {
			
			// Create client packet
			long time = System.currentTimeMillis();
			
			byte[] sequenceBytes = ByteBuffer.allocate(4).putInt(sequence).array();
			byte[] timeBytes = ByteBuffer.allocate(8).putLong(time).array();
			
			message = new byte[sequenceBytes.length + timeBytes.length];
			System.arraycopy(sequenceBytes, 0, message, 0, sequenceBytes.length);
			System.arraycopy(timeBytes, 0, message, sequenceBytes.length, timeBytes.length);
			
			InetAddress IPAddress = InetAddress.getByName(remoteHost);
			
			// Sending the packet
			
			DatagramPacket sendingPacket = new DatagramPacket(message, 0, message.length, IPAddress, remotePort);
			clientSocket.send(sendingPacket);
			packetsSent[sequence-1] = message;
			
			// Wait one second to send another packet
			TimeUnit.SECONDS.sleep(1);
			sequence++;
			
		}
		
		// In the end, show the summary and stop the thread
		clientReceiver.printSummary();
		thread.interrupt();
		
	}
	

}
