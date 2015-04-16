package peer;

import java.util.Scanner;

import networkManagement.Management;

/*
 * Each peer consists of a networkManagement instance which is containing one table of nodes
 * in the unstructured peer to peer network
 * One peer is attached to the network by connecting to some node of the network (constructor).
 * client establish connection to network with the ip address of the peer
 * client send requests to other peers 
 * server listen to new connected peer (delegate ip and port to the manager)
 * server response to incoming requests of other peers
 */
public class Peer {
	private Management manager = new Management();
	private Server server;
	private Applicant client;
	private Scanner inputScanner;
	Thread serverThread;
	Thread clientThread;
	private int initPort = -1;
	private String initIP = "localhost";
	
	// init peer
	public Peer() {
		// user enter ip and port of one peer in the network
		System.out.println("Enter IP or 0 to create new network");
		inputScanner = new Scanner(System.in);
		
		initIP = inputScanner.nextLine();
		if (initIP.equals("0")) // new network
			initPort = 0;
		else if (initIP.contains(":")){
			// <ip>:<port> format
			initPort = Integer.parseInt(initIP.split(":")[1]);
			initIP = initIP.split(":")[0];
		} else { // port separately entered
			while (readPort() == -1);
		}
		System.out.println("Enter name of peer:");
		String name = inputScanner.nextLine();
		server = new Server(manager, name); // listener for new peer
		client = new Applicant(initIP, initPort, manager, inputScanner); // client
		
	}

	/**
	 * create an start threads of listener and client
	 */
	public void start() {
		Thread serverThread = new Thread(server);
		Thread clientThread = new Thread(client);
		clientThread.run();
		if (client.started()){
			initServices();
		}
		serverThread.run();

		System.out.println("Peer shutdown");
	}
	// init command listener and pushing service for tables
	public void initServices(){
		new CommandListener(server).start(); // command listener
		Thread pushThread = new Thread(new PushingService()); // start pushing
																// service
		pushThread.start();
		manager.setPushServiceThread(pushThread);
	}
	// read initport
	public int readPort(){
		System.out.println("Enter port");
		try {
			initPort = Integer.parseInt(inputScanner.nextLine());
		} catch (NumberFormatException e){
			initPort = -1;
		}
		return initPort;
	}

	/**
	 * listen to the commands from the user Commands: 
	 * help - list all commands
	 * quit - exit peer 
	 * table - print table of peer 
	 * all - send a one-to-all message 
	 * send - send a message to other peer TODO
	 */
	private class CommandListener extends Thread {
		private Server server;
		private boolean closed = false;

		public CommandListener(Server server) {
			this.server = server;
		}

		public void run() {
			while (!closed) {
				// read command
				String command = inputScanner.nextLine();
				// quit peer -> disconnect and stop listener
				if (command.equals("quit")) {
					server.close();
					inputScanner.close();
					closed = true;
				}
				// print table of peers
				else if (command.equals("table")) {
					manager.getCurrentState();
				}
				// print possible commands
				else if (command.equals("help")) {
					System.out.println("Commands:\nquit - exit peer\ntable - list node table of peer\nall - send a one to all message\nsend - send a message to one peer\n");
				}
				// one to all message
				else if (command.equals("all")) {
					System.out.println("Enter message:");
					String info = inputScanner.nextLine();
					manager.oneToAll(info);
				}
				// contact one peer
				else if (command.equals("send")){
					System.out.println("Enter message:");
					String mes = inputScanner.nextLine();
					System.out.println("Enter name:");
					String target = inputScanner.nextLine();
					manager.contactPeer(target, mes);
				}
				
				else {
					System.out.println("no such command");
					System.out.println("Commands:\nquit - exit peer\ntable - list node table of peer\nall - send a one to all message\nsend - send a message to one peer\n");
				}
			}
		}
	}

	/**
	 * 
	 * share own nodes with arbitrary peer in the network in a periodically
	 * fashion
	 */
	private class PushingService implements Runnable {
		private boolean pushing = true;

		@Override
		public void run() {
			while (manager.isRunning()) {
				try {
					// share nodes every 10 sec
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					pushing = false;
				}
				if (pushing){
					manager.shareNodes();
				}
			}
		}

	}

	/**
	 * create peer
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Peer started");
		Peer p = new Peer();
		p.start();
	}
}
