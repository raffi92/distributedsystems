package peer;

import java.net.InetAddress;
import networkManagement.Management;
/*
 * Each peer consists of a networkManagement instance which is containing one table of nodes
 * in the unstructured peer to peer network
 * One peer is attached to the network by connecting to some node of the network (constructor).
 * client establish connection to network with the ip address of the peer
 * client send requests to other peers (open which services are provided)
 * server listen to new connected peer (delegate ip and port to the manager)
 * server response to incoming requests of other peers
 */
public class Peer {
	private Management manager = new Management();
	private Server server;
	private Client client;
	// shared variables between server and client of one peer
	// TODO
	
	public Peer (){
		// user enter ip and port of one peer in the network
		InetAddress initIP = null;
		int initPort = 0;
		server = new Server();
		client = new Client(initIP, initPort);
		Thread serverThread = new Thread(server);
		Thread clientThread = new Thread(client);
		serverThread.run();
		clientThread.run();
		
		// try connect to given peer (client) 
		// TODO
		
		// shutdown service for peer
		// TODO
		
	}
}
