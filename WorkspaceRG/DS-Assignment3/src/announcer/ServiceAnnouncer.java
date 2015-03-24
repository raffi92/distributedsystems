package announcer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class ServiceAnnouncer {
	int port;
	byte[] data = new byte[512];
	DatagramSocket socket;
	DatagramPacket packet;

	public ServiceAnnouncer(int port) {
		this.port = port;
	}

	public static void main(String[] args) {
		ServiceAnnouncer server = new ServiceAnnouncer(8001);
		server.startServer();
	}

	private void startServer() {
		System.out.println("ServiceAnnouncer started at port " + port);
		try {
			while (true) {
				socket = new DatagramSocket(port);

				packet = new DatagramPacket(data, data.length);
				System.out.println("Waiting for message...");
				socket.receive(packet);
				InetAddress address = packet.getAddress();
			    int client = packet.getPort();
			    int length = packet.getLength();
			    byte[] data = packet.getData();
			    
			    System.out.printf("Received message from %s of port %d with length %d: %s", address, client, length, new String(data,0,length));
			}
		} catch (IOException e) {
			System.out.println("Something went wrong with packet receiving/sending");
		} finally {
			socket.close();
		}

	}
}