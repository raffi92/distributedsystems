package peer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client implements Runnable{
	private int port = 0;
	private Socket client;
	public Client(InetAddress initIP, int initPort){
		// client send its InetAddress and port to the other peer
		// TODO InetAddress.getLocalHost()
		port = initPort;
	}
	@Override
	public void run() {
		if (port != 0){
			try {
				client = new Socket("localhost", port);
				System.out.println("client started");
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
