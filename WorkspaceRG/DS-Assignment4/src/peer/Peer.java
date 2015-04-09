package peer;

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
	private Scanner inputScanner;
	Thread serverThread;
	Thread clientThread;
	
	// init peer
	public Peer() {
		String initIP = "localhost";
		int initPort = 0;
		// user enter ip and port of one peer in the network
		System.out.println("Enter IP or 0 to create new network");
		inputScanner = new Scanner(System.in);
		// TODO fehlerbehandlung bei falschem input
		initIP = inputScanner.nextLine();
		if (initIP.equals("0")) // new network
			initPort = 0;
		else if (initIP.split(":")[0].length() > 0 && initIP.split(":")[1].length() > 0){
			// <ip>:<port> format
			initPort = Integer.parseInt(initIP.split(":")[1]);
			initIP = initIP.split(":")[0];
		}
		else {	// port separately entered
			System.out.println("Enter port");
			initPort = inputScanner.nextInt();
		}
		server = new Server(manager); // listener
		new CloseListener(server).start(); // close listener
		client = new Client(initIP, initPort, manager); // client
	}
	/**
	 * create an start threads of listener and client
	 */
	public void start() {
		Thread serverThread = new Thread(server);
		Thread clientThread = new Thread(client);
		clientThread.run();
		serverThread.run();

		System.out.println("Peer shutdown");
	}

	
	/**
	 * listen if peer should be exited
	 */
	private class CloseListener extends Thread {
		private Server server;
		private boolean closed = false;

		public CloseListener(Server server) {
			this.server = server;
		}

		public void run() {
			while (!closed) {
				if (inputScanner.next().equals("quit")) {
					server.close();
					inputScanner.close();
					closed = true;
				}
			}
		}
	}
	/**
	 * create peer
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Peer started");
		Peer p = new Peer();
		p.start();
	}
}
