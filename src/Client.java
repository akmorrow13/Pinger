import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
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
		
		// Data to the final report
		
		long minimumRtt = 0;
		long averageRtt = 0;
		long maximumRtt = 0;
		
		int numLostPackets = 0;
		
		ArrayList<Long> rttList = new ArrayList<Long>();
		
		
		DatagramSocket clientSocket = new DatagramSocket(null);
		int sequence = 1; // In the assignment, the fist packet is the number 1
		
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
			
			// Sending the packet
			
			DatagramPacket sendingPacket = new DatagramPacket(message, 0, message.length, IPAddress, remotePort);
			clientSocket.send(sendingPacket);
			
			// Receiving the packet from the Server
			
			byte[] serverBuffer = new byte[12];
			DatagramPacket receivingPacket = new DatagramPacket(serverBuffer, serverBuffer.length);	
			clientSocket.receive(receivingPacket);
			long receivingTime = System.currentTimeMillis();
			
			ByteBuffer bb = ByteBuffer.wrap(serverBuffer);
			
			// Check if the packet sent is the same of the received
			
			boolean packetsEqual = true;
			
			for(int j = 0; j < serverBuffer.length; j++){
				
				// If one byte is different, the packets are not equal
				
				if (serverBuffer[j] != message[j]){
					packetsEqual = false;
					break;
				}
				
			}
			
			if(packetsEqual){
				
				int packetSize = receivingPacket.getLength();
				String packetRemoteHost = receivingPacket.getAddress().getHostName();
				int packetSequence = bb.getInt(); // Actually, it is necessary to get the first 4 bytes from the array
				long packetTime = bb.getLong();
				
				long rtt = receivingTime - packetTime; // The time now minus the original time when the packet was sent
				
				rttList.add(rtt);
				
				System.out.println("size="+ packetSize +" from="+ packetRemoteHost +" seq="+ packetSequence +" rtt="+ rtt +" ms");	
				
			}else{
				
				numLostPackets++;
				
				System.out.println("A packet was lost...");
			}
			
			
			
			// Check if packet received is equal to packet sent
			
			
			
			//if (IPAddress != packet.getAddress() || remotePort != packet.getPort()) {
				// this was a lost packet
			//}
			
			TimeUnit.SECONDS.sleep(1);
			sequence++;
		}
		
		Collections.sort(rttList);
		
		minimumRtt = (Long) rttList.get(0);
		maximumRtt = (Long) rttList.get(rttList.size()-1);
		
		for(Long rttI: rttList){
			averageRtt += rttI/(rttList.size());
		}
		
		System.out.println("sent="+ packetCount +" received="+ (packetCount-numLostPackets)  
				+" lost="+((numLostPackets/packetCount)*100) 
				+"% rtt min/avg/max="+ minimumRtt +"/"+ averageRtt +"/"+maximumRtt +" ms");
		
		
	}
	
	
	

	public void printInfo(DatagramPacket returnedPacket) {
		
	}
	
	public void printSummary(int packetCount, int packetsReceived, int[] tripTime) {
		
	}
}
