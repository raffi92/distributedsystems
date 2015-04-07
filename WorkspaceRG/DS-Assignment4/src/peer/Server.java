package peer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import networkManagement.Management;

/*
 * This class act like a server and listens to new incomming nodes.
 */
public class Server implements Runnable {
	private ServerSocket serversock;
	private Management manager;
	private int port = 0; // 0 means any free port
	private boolean running = true;

	public Server(Management manager) {
		this.manager = manager;
		// create new server socket
		try {
			serversock = new ServerSocket(port);
			manager.setListener(serversock);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// set local peer address of server that client can inform other peers
		manager.setLocalPeerAddress(serversock);
	}

	@Override
	public void run() {
		// info server ip and port
		try {
			System.out.println("Peer listener run on port " + InetAddress.getLocalHost().getHostAddress() + ":" + serversock.getLocalPort());
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// server loop waits for clients
		while (running) {
			try {
				new Listener(serversock.accept()).start();
			} catch (IOException e) {
				if (e.getMessage().equals("Socket is closed") || e.getMessage().equals("Socket closed")) {
					// System.out.println("Server closed");
				} else {
					e.printStackTrace();
				}
			}
		}

	}

	private class Listener extends Thread {
		private Socket socket;

		public Listener(Socket socket) {
			this.socket = socket;
		}

		public void run() {
			System.out.println("new peer " + socket.getRemoteSocketAddress());

			try {
				DataInputStream in = new DataInputStream(socket.getInputStream());
				DataOutputStream out = new DataOutputStream(socket.getOutputStream());
				String newNode = in.readUTF();
				manager.addEntry(newNode);
				out.writeBoolean(true);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	// quit server
	public void close() {
		running = false;
		manager.disconnection();
	}

}
