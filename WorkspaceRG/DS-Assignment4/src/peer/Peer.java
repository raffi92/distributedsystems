package peer;

import java.net.InetAddress;
import java.util.Scanner;

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
	Thread serverThread;
	Thread clientThread;
	// shared variables between server and client of one peer
	// TODO
	
	public Peer (){
		// user enter ip and port of one peer in the network
		InetAddress initIP = null;
		int initPort = 0;
		System.out.println("Enter port to connect to other peer or 0 to create new network");
		Scanner input = new Scanner(System.in);
		initPort = input.nextInt();
		server = new Server();
		new CloseListener(server).start();
		client = new Client(initIP, initPort);
		
	}
	
	public void start(){
		Thread serverThread = new Thread(server);
		Thread clientThread = new Thread(client);
		clientThread.run();
		serverThread.run();
		
		
		// try connect to given peer (client) 
		// TODO
		
		// shutdown service for peer
		// TODO	
	}
	
	
	public static void main(String[] args) {
		System.out.println("Peer started");
		Peer p = new Peer();
		p.start();
	}
	
	private class CloseListener extends Thread {
		private Server server;
		private boolean closed = false;
		public CloseListener(Server server){
			this.server = server;
		}
		public void run() {
			Scanner input = new Scanner(System.in);
			while (!closed){
				if (input.next().equals("quit")){
					server.close();
					input.close();
					closed = true;
				}
			}
		}
	}
}
