package peer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
/*
 * This class act like a server and listens to new incomming nodes.
 */
import java.net.Socket;
import java.util.Scanner;

import networkManagement.Management;

public class Server implements Runnable {
	private ServerSocket socket;
	private Management manager;
	private int port = 8005;
	private boolean running = true;
	
	public Server(Scanner input, Management manager){
		System.out.println("Enter port on which server should listen");
		port = input.nextInt();
		this.manager = manager;
	}

	@Override
	public void run() {
		try {
			socket = new ServerSocket(port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Peer server run on port " + port);

		while (running) {
			try {
				new Listener(socket.accept()).start();
			} catch (IOException e) {
				if (e.getMessage().equals("Socket is closed") || e.getMessage().equals("Socket closed")) {
					//System.out.println("Server closed");
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
			System.out.println("Just connected to client"
					+ socket.getRemoteSocketAddress());
			try {
				DataInputStream in = new DataInputStream(socket.getInputStream());
				DataOutputStream out = new DataOutputStream(
						socket.getOutputStream());
				String newNode = in.readUTF();
				manager.addEntry(newNode);
				System.out.println(newNode);
				
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

	public void close() {
		running = false;
		try {
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Server closed");
	}

}
