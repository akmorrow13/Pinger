import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;


public class ClientReceiver implements Runnable {
	
	// Attributes

	DatagramSocket clientSocket;
	DatagramPacket receivingPacket;
	
	byte[][] packetsSent;
	byte[][] packetsReceived;
	byte[] serverBuffer;
	

	long minimumRtt = 0;
	float averageRtt = 0;
	long maximumRtt = 0;
	
	int numLostPackets = 0;
	int packetCount = 0;

	ArrayList<Long> rttList = new ArrayList<Long>();


	// Constructors
	
	public ClientReceiver(byte[][] packetsSent, DatagramSocket clientSocket){
		this.packetsSent = packetsSent;
		this.clientSocket = clientSocket;
		
		packetCount = packetsSent.length;
		packetsReceived = new byte[packetCount][12];
	}
	
	// Methods
	
	// Method executed by the start() call

	@Override
	public void run() {	

		int order = 0;

		while(true){

			serverBuffer = new byte[12];
			receivingPacket = new DatagramPacket(serverBuffer, serverBuffer.length);

			// Waiting receive a new packet

			try {
				clientSocket.receive(receivingPacket);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			packetsReceived[order] = serverBuffer;
			long receivingTime = System.currentTimeMillis();


			// Check if the packet sent is equal to the received

			boolean packetsEqual = true;

			for(int i = 0; i < serverBuffer.length; i++){

				// If one byte is different, the packets are not equal

				if (packetsSent[order][i] != packetsReceived[order][i]){
					packetsEqual = false;
					break;
				}

			}
			


			if(packetsEqual){

				ByteBuffer bb = ByteBuffer.wrap(serverBuffer);

				int packetSize = receivingPacket.getLength();
				String packetRemoteHost = receivingPacket.getAddress().getHostName();
				
				int packetSequence = bb.getInt();
				long packetTime = bb.getLong();

				long rtt = receivingTime - packetTime; // The time now minus the original time when the packet was sent
				rttList.add(rtt);

				System.out.println("size="+ packetSize +" from="+ packetRemoteHost +" seq="+ packetSequence +" rtt="+ rtt +" ms");	

			}else{

				numLostPackets++;

				System.out.println("This packet was lost...");
			}
			
			order++;


		}
		

	}
	
	// This method prints the final summary
	
	public void printSummary(){
		
		Collections.sort(rttList);
		
		minimumRtt = (Long) rttList.get(0);
		maximumRtt = (Long) rttList.get(rttList.size()-1);
		
		float rttListSize = (float) rttList.size();
				
		for(Long rttI: rttList){
			
			
			averageRtt += (float) rttI/rttListSize;

		}

		System.out.println("sent="+ packetCount +" received="+ (packetCount-numLostPackets)  
				+" lost="+((numLostPackets/packetCount)*100) 
				+"% rtt min/avg/max="+ minimumRtt +"/"+ averageRtt +"/"+maximumRtt +" ms");
	}
	
}
