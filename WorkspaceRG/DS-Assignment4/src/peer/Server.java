package peer;

import java.io.IOException;
import java.net.ServerSocket;
/*
 * This class act like a server and listens to new incomming nodes.
 */
import java.net.Socket;

public class Server implements Runnable {
	private ServerSocket socket;
	private int port = 8003;
	private boolean running = true;

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
			System.out.println("do something");
			try {
				socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void close() {
		try {
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Server closed");
	}

}
