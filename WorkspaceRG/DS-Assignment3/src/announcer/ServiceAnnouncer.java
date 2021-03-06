package announcer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class ServiceAnnouncer {
	int port;
	byte[] data = new byte[512];
	static DatagramSocket socket;
	DatagramPacket packet;
	int timeout = 6000;
	
	public ServiceAnnouncer(int port) {
		this.port = port;
	}

	private void startServer() {
		System.out.println("ServiceAnnouncer started at port " + port);
		try {
			socket = new DatagramSocket(port);
			while (true) {
				packet = new DatagramPacket(data, data.length);
				System.out.println("Waiting for message...");
				//socket.setSoTimeout(timeout);
				socket.receive(packet);
				InetAddress address = packet.getAddress();
			    int client = packet.getPort();
			    int length = packet.getLength();
			    byte[] data = packet.getData();
			    System.out.printf("Received message from %s of port %d with length %d: %s", address, client, length, new String(data,0,length));
			    System.out.println("");
			}
		} catch (IOException e) {
			System.out.println("Server shutdown");
		} finally {
			socket.close();
		}

	}
	
	public static void main(String[] args) {
		ServiceAnnouncer server = new ServiceAnnouncer(8001);
		Shutdown shut = new Shutdown();
		shut.start();
		server.startServer();
	}
}