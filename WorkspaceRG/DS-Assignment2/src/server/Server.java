package server;

import java.net.*;
import java.io.*;

import protocol.Protocol;

public class Server {
	static int port;
	
	public static void main(String[] args){
		port = Protocol.getServerPort();
		ServerSocket server;
		Socket socket;
		try {
			server = new ServerSocket(port);
				System.out.println("Waiting for client");
				socket = Protocol.waitForAccept(server);
				System.out.println("Just connected to client" + socket.getRemoteSocketAddress());
				Protocol.reply(socket);
				socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
