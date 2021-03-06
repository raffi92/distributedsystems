package locator;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Simple client/server application
 *
 */

public class ServiceLocator {
	int port;
	DatagramSocket socket;
	DatagramPacket packet;
	String hello = new String("Distributed Systems");
	
	public ServiceLocator(int port){
		this.port = port;
	}
	
	public void startClient(InetAddress inet){
		byte[] data = hello.getBytes();
		try {
		packet = new DatagramPacket(data, data.length, inet,port);
		socket = new DatagramSocket();
		System.out.println("Sending packet with broadcast from client: " + packet.getAddress());
		socket.send( packet );
		} catch (IOException e) {
			System.out.println("Something went wrong with the message");
		}
	}
	
	public InetAddress getIP(){
		byte adr = (byte) 255;
		byte[] address = { adr, adr, adr, adr };
		//InetAddress internet = InetAddress.getByName("localhost");
		InetAddress internet = null;
		try {
			internet = InetAddress.getByAddress(address);
		} catch (UnknownHostException e) {
			System.out.println("Can't get address");
		}
		return internet;
	}
	
	public static void main(String[] args){
		ServiceLocator client = new ServiceLocator(8001);
		InetAddress inet = client.getIP();
		client.startClient(inet);
	}
}