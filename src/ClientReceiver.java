import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;


public class ClientReceiver implements Runnable {

	// Attributes

	private DatagramSocket clientSocket;
	private DatagramPacket receivingPacket;

	private byte[][] packetsSent;
	private byte[] serverBuffer;


	private long minimumRtt = 0;
	private float averageRtt = 0;
	private long maximumRtt = 0;

	private int numLostPackets = 0;
	private int packetCount = 0;

	private ArrayList<Long> rttList = new ArrayList<Long>();

	private int order = 1;
	boolean finalized = false;


	// Constructors

	public ClientReceiver(byte[][] packetsSent, DatagramSocket clientSocket){
		this.packetsSent = packetsSent;
		this.clientSocket = clientSocket;

		// After 5 seconds, the client raise a timeout

		try {
			clientSocket.setSoTimeout(5*1000);
		} catch (SocketException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}


		packetCount = packetsSent.length;

	}

	// Methods

	// Method executed by the start() call

	@Override
	public void run() {	

		while(true){

			// Verify if all packets sent was checked

			if(order-1 == packetCount){
				finalized = true;
				break;
			}


			serverBuffer = new byte[12];
			receivingPacket = new DatagramPacket(serverBuffer, serverBuffer.length);


			// Waiting receive a new packet

			try {
				clientSocket.receive(receivingPacket);
			} catch(SocketTimeoutException e){
				System.out.println("Timed out");
				numLostPackets++;
				order++;
				continue;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			long receivingTime = System.currentTimeMillis();

			ByteBuffer bb = ByteBuffer.wrap(serverBuffer);
			int sequencePacketReceived = bb.getInt(0);


			// Check if the packet sent is equal to the received

			boolean packetsEqual = true;

			for(int i = 0; i < serverBuffer.length; i++){

				// If one byte is different, the packets are not equal

				if (packetsSent[sequencePacketReceived-1][i] != serverBuffer[i]){
					packetsEqual = false;
					break;
				}

			}

			// If a packet is received before your previous packet, so the previous was lost.

			if(sequencePacketReceived != order){
				numLostPackets++;
				order = sequencePacketReceived;
			}



			if(packetsEqual){

				int packetSize = receivingPacket.getLength();
				String packetRemoteHost = receivingPacket.getAddress().getHostAddress();

				int packetSequence = bb.getInt();
				long packetTime = bb.getLong();

				long rtt = receivingTime - packetTime; // The time now minus the original time when the packet was sent
				rttList.add(rtt);

				System.out.println("size="+ packetSize +" from="+ packetRemoteHost +" seq="+ packetSequence +" rtt="+ rtt +" ms");	

			}else{

				numLostPackets++;

				System.out.println("This packet is broken/ wrong...");
			}

			order++;

		}

	}

	// This method prints the final summary

	public void printSummary(){

		if(rttList.size() > 1) {

			Collections.sort(rttList);

			minimumRtt = (Long) rttList.get(0);
			maximumRtt = (Long) rttList.get(rttList.size()-1);

			float rttListSize = (float) rttList.size();

			float percentagelostPackets =  (((float) 100*numLostPackets)/packetCount);

			for(Long rttI: rttList){

				averageRtt += (float) rttI/rttListSize;
			}

			System.out.println("sent="+ packetCount +" received="+ (packetCount-numLostPackets)  
					+" lost="+percentagelostPackets
					+"% rtt min/avg/max="+ minimumRtt +"/"+ averageRtt +"/"+maximumRtt +" ms");
		} else {

			System.out.println("No packet received");
		}
	}

	public boolean isFinalized(){
		return this.finalized;
	}

}
