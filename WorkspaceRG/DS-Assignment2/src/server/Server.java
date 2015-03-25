package server;

import java.net.*;
import java.util.Scanner;
import java.io.*;

import protocol.Protocol;

public class Server {
	static int port;
	static boolean running = true;
	static ServerSocket server;

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
			} catch (IOException e) {
				if (e.getMessage().equals("Socket closed")) {
					System.out.println("Server shutdown");
					running = false;
				} else {
					e.printStackTrace();
				}

			}
		}

	}
	
	public static void remoteQuitServer() {
		try {
			server.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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

//	private static class CloseListener extends Thread {
//		public void run() {
//			Scanner input = new Scanner(System.in);
//			input.next().equals("quit");
//			try {
//				server.close();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			input.close();
//		}
//	}
}
