package server;

import java.net.*;
import java.util.Scanner;
import java.io.*;

import protocol.Protocol;

public class Server {
	static int port;
	static boolean running = true;
	static ServerSocket server;
	static int activeClients = 0; 

	public static void main(String[] args) {
		port = Protocol.getServerPort();
		try {
			server = new ServerSocket(port);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		System.out.println("Waiting for client\n");
		//new CloseListener().start();
		while (running) {
			try {
				new Listener(server.accept()).start();
				activeClients++;
			} catch (IOException e) {
				if (e.getMessage().equals("Socket closed")) {
					if (activeClients == 1){
						System.out.println("Server shutdown. " + activeClients + " Client are still active.");	
					}
					if (activeClients > 1)
						System.out.println("Server shutdown. " + activeClients + " Clients are still active.");
					running = false;
				} else {
					e.printStackTrace();
				}

			}
		}

	}

	private static class Listener extends Thread {
		private Socket socket;

		public Listener(Socket socket) {
			this.socket = socket;
		}

		public void run() {
			System.out.println("Just connected to client"
					+ socket.getRemoteSocketAddress());
			new Protocol().reply(socket);

			try {
				socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void decreaseActive(Socket socket){
		System.out.println("Client" + socket.getRemoteSocketAddress() + " terminated");
		activeClients--;
	}
	
	public static void remoteQuitServer(Socket socket) {
		decreaseActive(socket);
		try {
			server.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
