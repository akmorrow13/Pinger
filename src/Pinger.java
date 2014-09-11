import java.io.IOException;


public class Pinger {
	
	
	public static void main(String[] args) throws IOException, InterruptedException {
		
		int argCount = args.length;
		
		int localPort = -1;
		String remoteHost = "";
		int remotePort = -1;
		int packetCount = -1;
		
		// Parse parameters
		
		for (int i = 0; i < argCount; i+=2) {
			
			if (args[i].equals("-l")) {
				localPort = Integer.parseInt(args[i+1]);
				if(!isPortValid(localPort)){
					System.out.println("Local port out of bounds");
					System.exit(-1);
				}
			} else if (args[i].equals("-h")) {
				remoteHost = args[i+1];
			} else if (args[i].equals("-r")) {
				remotePort = Integer.parseInt(args[i+1]);
				if(!isPortValid(remotePort)){
					System.out.println("Remote port out of bounds");
					System.exit(-1);
				}
			}else if (args[i].equals("-c")) {
				packetCount = Integer.parseInt(args[i+1]);
			} else {
				System.out.println("Invalid option " + args[i]);
				System.exit(-1);
			}
		}
		
		// Server mode
		
		if (argCount == 2) { 
			
			Server server = new Server(localPort);
			server.listenClients();
		
		// Client mode
		} else if (argCount == 8) { 
			
			Client client = new Client(localPort, remoteHost, remotePort, packetCount);
			client.sendPacket();
		
		// Invalid parameters
		} else {
			
			System.out.println("Error: missing or additional arguments");
			System.exit(-2);
		}
			 
		// If everything finished OK
		System.exit(0);	
		
	}
	
	//Method to check if the port is valid
	
	private static boolean isPortValid(int port){
		if (port >= 1024 && port < 65536) {
			return true;
		} else {
			return false;
		}
	}

}
