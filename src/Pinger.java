import java.io.IOException;
import java.net.DatagramSocket;


public class Pinger {
	
	
	public static void main(String[] args)
	throws IOException
	{
		// mode to determine client or server mode. 0 is client mode, 1 is server mode
		int mode = 1;
		
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-c")) {
				mode = 0;
				break;
			}
		}
		
		if (mode == 0) {
			// client
		} else {
			// server
		}
		
		
		
		
		
		
		int argCount = args.length;
		int localPort = -1;
		String remoteHost = "";
		String remotePort = "";
		int packetCount = -1;
		
		if (argCount != 8) {
			System.out.println("Error: missing or additional arguments");
			System.exit(-1);
		}
		
		// parse options
		for (int i = 0; i < argCount; i+=2) {
			
			if (args[i].equals("-l")) {
				localPort = Integer.parseInt(args[i+1]);
			} else if (args[i].equals("-h")) {
				remoteHost = args[i+1];
			} else if (args[i].equals("-r")) {
				remotePort = args[i+1];
			}else if (args[i].equals("-c")) {
				packetCount = Integer.parseInt(args[i+1]);
			} else {
				System.out.println("Invalid option " + args[i]);
				System.exit(-1);
			}
			
		}
		
		if (localPort < 1024 || localPort >= 65536) {
			System.out.println("local port out of bounds");
			System.exit(-1);
		}
		
		System.out.println(localPort+remoteHost+remotePort+packetCount);
		
		// Create Datagram Socket
		DatagramSocket s = new DatagramSocket(null);
		
	}

}
