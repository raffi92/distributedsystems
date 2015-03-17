package client;

import java.net.*;
import java.io.*;

import protocol.Protocol;

public class Client {

	public static void main(String[] args){
		int serverport = Protocol.getServerPort();
		try {
			Socket client = new Socket("localhost",serverport);
			Protocol.request(client);
			client.close();
		} catch (UnknownHostException error) {
			System.out.println("Server not found");
		} catch (IOException error) {
			error.printStackTrace();
		}
	}
}
