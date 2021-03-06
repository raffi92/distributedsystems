package peer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import networkManagement.Management;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/*
 * This class act like a server and listens to new incomming nodes.
 */
public class Server implements Runnable {
	private ServerSocket serversock;
	private Management manager;
	private int port = 0; // 0 means any free port
	private boolean running = true;

	public Server(Management manager, String name) {
		this.manager = manager;
		// create new server socket
		try {
			serversock = new ServerSocket(port);
			manager.setListener(serversock);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// set local peer address of server that client can inform other peers
		manager.setLocalPeerAddress(serversock, name);
	}

	
	public void run() {
		// info server ip and port
		try {
			System.out.println("Peer listener run on port " + InetAddress.getLocalHost().getHostAddress() + ":" + serversock.getLocalPort());
		} catch (UnknownHostException e1) {
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

	// quit server
	public void close() {
		running = false;
		manager.quit();
	}
	// listen for input
	private class Listener extends Thread {
		private Socket socket;

		public Listener(Socket socket) {
			this.socket = socket;
		}

		public void run() {
			try {
				DataInputStream in = new DataInputStream(socket.getInputStream());
				DataOutputStream out = new DataOutputStream(socket.getOutputStream());
				JSONObject message = new JSONObject(in.readUTF());

				// add entries from other node
				if (message.has("table")) {
					JSONArray tableArray = (JSONArray) message.get("table");
					// parse and add entries to own table
					manager.parseJsonArray(tableArray);
					// delete double entries and self reference
					manager.deleteInvalid();
					// delete nodes removed from sender
					if (message.has("removed"))
						manager.removeInvalidNodes(message.getJSONArray("removed"));
					// respond with own table
					JSONObject respond = manager.buildJsonObjectOfTable();
					out.writeUTF(respond.toString());
				}
				// forward one-to-all message
				if (message.has("all")) {
					manager.forwardOneToAll(message);
				}
				// lookup for one-to-one message
				if (message.has("target")) {
					manager.lookUp(message);
				}
				
				// send one-to-one message
				if (message.has("targetIP")){
					String ip = null;
					int port = 0;
					String mes = null;
					ip = message.getString("targetIP");
					port = message.getInt("targetPort");
					mes = message.getString("message");
					manager.sendOneToOne(ip, port, mes);
				}
				
				// receive one-to-one message
				if (message.has("oneToOneMessage")){
					System.out.println(message.get("oneToOneMessage"));
				}
				
				// receiver not reachable
				if (message.has("reachable")){
					System.out.println("Receiver not reachable");
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			manager.disconnecting(socket);
		}
	}
}
