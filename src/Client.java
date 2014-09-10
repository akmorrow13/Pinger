import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;



public class Client {

	private int localPort;
	private String remoteHost;
	private int remotePort;
	private int packetCount;
	private byte message[];
	
	public Client(int localPort, String remoteHost, int remotePort, int packetCount) {
		this.localPort = localPort;
		this.remoteHost = remoteHost;
		this.remotePort = remotePort;
		this.packetCount = packetCount;
		
	}
	
	public void sendPacket() throws InterruptedException, IOException {
		DatagramSocket clientSocket = new DatagramSocket(null);
		int sequence = 0;
		
		try {
			clientSocket = new DatagramSocket(localPort);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		
		for (int i = 0; i < packetCount; i++) {
			
			// Create client packet
			long time = System.currentTimeMillis();
			
			byte[] sequenceBytes = ByteBuffer.allocate(4).putInt(sequence).array();
			byte[] timeBytes = ByteBuffer.allocate(8).putLong(time).array();
			
			message = new byte[sequenceBytes.length + timeBytes.length];
			System.arraycopy(sequenceBytes, 0, message, 0, sequenceBytes.length);
			System.arraycopy(timeBytes, 0, message, sequenceBytes.length, timeBytes.length);
			
			InetAddress IPAddress = InetAddress.getByName(remoteHost);
			
			DatagramPacket packet = new DatagramPacket(message, 0, message.length, IPAddress, remotePort);
			clientSocket.send(packet);
			clientSocket.receive(packet);
			
			// Check if packet is from the correct remote Host
			if (IPAddress != packet.getAddress() || remotePort != packet.getPort()) {
				// this was a lost packet
			}
			
			TimeUnit.SECONDS.sleep(1);
			sequence++;
		}
	}
	
	
	

	public void printInfo(int size, String IP, int sequence, int time) {
		
	}
	
	public void printSummary(int packetCount, int packetsReceived, int[] tripTime) {
		
	}
}
