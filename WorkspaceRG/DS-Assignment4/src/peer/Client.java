package peer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import networkManagement.Management;

public class Client implements Runnable{
	private int port = 0;
	private Socket client;
	private Management manager;
	public Client(InetAddress initIP, int initPort, Management manager){
		// client send its InetAddress and port to the other peer
		// TODO InetAddress.getLocalHost()
		port = initPort;
		this.manager = manager;
	}
	@Override
	public void run() {
		if (port != 0){
			try {
				client = new Socket("localhost", port);
				System.out.println("peer attached to network");
				OutputStream sendToServer = client.getOutputStream();
				DataOutputStream out = new DataOutputStream(sendToServer);
				InputStream receivedFromServer = client.getInputStream();
				DataInputStream in = new DataInputStream(receivedFromServer);
				String address = "127.0.0.1:" + port;
				out.writeUTF(address);
				System.out.println(in.readBoolean());
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			System.out.println("new network created");
		}
	}

}
