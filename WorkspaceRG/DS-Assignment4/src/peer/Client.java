package peer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import networkManagement.Management;

public class Client implements Runnable {
	private int initPort = 0;
	private String initIp = null;
	private Socket client;
	private Management manager;

	public Client(String initIp, int initPort, Management manager) {
		this.initPort = initPort;
		this.initIp = initIp;
		this.manager = manager;
	}

	@Override
	public void run() {
		// initport zero denotes new network
		if (initPort != 0) {
			try {
				// create socket
				client = manager.connecting(initPort, initIp);
				// add init node to table
				manager.addEntry(initIp + ":" + initPort);
				// create input and output streams
				OutputStream sendToServer = client.getOutputStream();
				DataOutputStream out = new DataOutputStream(sendToServer);
				InputStream receivedFromServer = client.getInputStream();
				DataInputStream in = new DataInputStream(receivedFromServer);
				// send own node to other peer
				String address = manager.getSelfNode().getIP() + ":" + manager.getSelfNode().getPort();
				out.writeUTF(address);
				if (in.readBoolean()) {
					System.out.println("successfully attached to network");
				}
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("new network created");
		}
		
	}

}
