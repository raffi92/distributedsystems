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
 * TODO fehlerbehandlung bei falschen eingaben vom user
 * TODO fehlerbehandlung bei peer die bereits offliner sind. bei tabellenaustausch bereits implementiert, aber bei one to all message fehlt das noch
 * 
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
		System.out.println("Enter IP [<ip>:<port>] or 0 to create new network");
		inputScanner = new Scanner(System.in);
		// TODO fehlerbehandlung bei falschem input
		initIP = inputScanner.nextLine();
		if (initIP.equals("0")) // new network
			initPort = 0;
		else if (initIP.contains(":")){
			// <ip>:<port> format
			initPort = Integer.parseInt(initIP.split(":")[1]);
			initIP = initIP.split(":")[0];
		}
		else { // port separately entered
			System.out.println("Enter port");
			initPort = Integer.parseInt(inputScanner.nextLine());
		}
		
		System.out.println("Enter name of peer:");
		String name = inputScanner.nextLine();
		server = new Server(manager, name); // listener for new peer
		new CommandListener(server).start(); // command listener
		Thread pushThread = new Thread(new PushingService()); // start pushing
																// service
		pushThread.start();
		manager.setPushServiceThread(pushThread);
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
				if (command.equals("table")) {
					manager.getCurrentState();
				}
				// print possible commands
				if (command.equals("help")) {
					System.out.println("Commands:\nquit - exit peer\ntable - list node table of peer\nall - send a one to all message\nsend - send a message to one peer\n");
				}
				// one to all message
				if (command.equals("all")) {
					System.out.println("Enter message:");
					String info = inputScanner.nextLine();
					manager.oneToAll(info);
				}
				// contact one peer
				if (command.equals("send")){
					System.out.println("Enter message:");
					String mes = inputScanner.nextLine();
					System.out.println("Enter name:");
					String target = inputScanner.nextLine();
					manager.contactPeer(target, mes);
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
