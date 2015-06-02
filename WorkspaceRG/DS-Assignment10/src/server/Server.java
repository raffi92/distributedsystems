package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import encryption.EncryptionIF;
import encryption.RC4;
import encryption.SimpleEncryption;

public class Server {
	int port = 11111;
	private ServerSocket ssocket;
	private Socket client;
	private String message;
	private String encrypted;
	private EncryptionIF method;
	
	public Server(String [] args){
		try {
			if (args.length != 2){
				System.out.println("Usage: java server.class [key] [method] Wrong number of parameter. \nArgument 1 must be the key\nArgument 2 must be the selected method\n[method]: \n0 ... SimpleEncryption\n1 ... RC4 Encryption\n");
				System.exit(0);
			}
			ssocket= new ServerSocket(port);
			System.out.println("Waiting for client");
			client = connect();
			method = null;
			if (Integer.parseInt(args[1]) == 0)
				method = new SimpleEncryption();
			if (Integer.parseInt((args[1])) == 1)
				method = new RC4();
			if (method == null){
				System.out.println("Wrong method: \n0 ... SimpleEncryption\n1 ... RC4 Encryption\n");
				System.exit(0);
			}
				
			method.setKey(args[0]);
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
		int [] toDecrypt = method.StringToIntArray(message);	// TODO jetzt liest bufferedReader in char[], dann wird string gebaut, dann string wieder zu int [] konvertiert --> vereinfachen
		return method.decrypt(toDecrypt);
	}
	
	private void printAll(){
		System.out.println("++++++++++++++++++++++++++++++++");
		System.out.println("Received message: " + message);
		System.out.println("Decrypted message: " + encrypted);
	}
	
	public static void main(String[] args){
		new Server(args);
	}
}
