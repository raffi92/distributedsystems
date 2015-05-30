package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	int port = 11111;
	private ServerSocket ssocket;
	private Socket client;
	private String keyString;
	private int key;
	private String message;
	private String decrypted;
	private String encrypted;
	
	public Server(){
		try {
			ssocket= new ServerSocket(port);
			System.out.println("Waiting for client");
			client = connect();
			System.out.println("connected");
			encrypted = readMsg(client);
			printAll();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Socket connect(){
		try {
			return ssocket.accept();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	void writeMsg(Socket socket, String message) throws IOException {
		PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
		printWriter.print(message);
		printWriter.flush();
	}
	
	public String readMsg(Socket socket) throws IOException {
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		char[] buffer = new char[500];
		int amount = bufferedReader.read(buffer, 0, 500);
		message = new String(buffer, 0, amount);
		getKey(message);
		return decode(decrypted,keyString);
	}
	
	private void getKey(String message) {
		StringBuilder cache = new StringBuilder();
		int i = message.length()-1;
		for(; i > 0 ; i--){
			if(message.charAt(i) == '+'){
				decrypted = message.substring(0,i);
				int j = i++;
				for(;j < message.length();j++){
					cache.append(message.charAt(j));
				}
				int keylength = Integer.parseInt(cache.toString());
				keyString = decrypted.substring(decrypted.length()-keylength,decrypted.length());
				decrypted = decrypted.substring(0,decrypted.length()-keylength);
				return;
			}
		}	
	}

	private void printAll(){
		System.out.println("++++++++++++++++++++++++++++++++");
		System.out.println("Received message: " + message);
		System.out.println("Decrypted message: " + encrypted);
	}
	
	private String decode(String out, String keyS)
	{
		key = Integer.parseInt(keyS);
		String in = "";
		key = 98 - key;
		for (int i = 0; i < out.length(); i++) {
			char ch = out.charAt(i);
			int cache = (' ' + ((ch - ' ' + key) % 98)) + 98;
			ch = (char) cache;
			in += ch;
		}
		return in.toString();
	}
	
	
	public static void main(String[] args){
		Server server = new Server();
	}
}
