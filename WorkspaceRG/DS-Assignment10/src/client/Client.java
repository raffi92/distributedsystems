package client;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.PublicKey;
import java.util.Scanner;

import encryption.EncryptionIF;
import encryption.RC4;
import encryption.SimpleEncryption;
import encryption.RSA;

public class Client {
		private String ip = "127.0.0.1"; // localhost
		private int port = 11111;
		private Socket socket;
		private EncryptionIF method;
		
		public Client(String[] args){
			try {
				String message;
				socket = new Socket(ip,port);
				method = null;
				if (args.length == 0){
					method = new RSA();
					message = enterMessage();
					writeRSAMsg(socket,message);
				}
				else if (args.length == 2){
					if (Integer.parseInt(args[1]) == 0)
						method = new SimpleEncryption();
					if (Integer.parseInt((args[1])) == 1)
						method = new RC4();
					method.setKey(args[0]);
					message = enterMessage();
					writeMsg(socket,message);
				}
				else {
					System.out.println("Usage: java client.class [key] [method] Wrong number of parameter. \nArgument 1 must be the key\nArgument 2 must be the selected method\n[method]: \n0 ... SimpleEncryption\n1 ... RC4 Encryption\n");
					System.exit(0);
				}
				if (method == null){
					System.out.println("Wrong method: \n0 ... SimpleEncryption\n1 ... RC4 Encryption\n");
					System.exit(0);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	public String enterMessage(){
		Scanner scanner = new Scanner(System.in); 
		System.out.println("Please enter your message");
		String tmp = scanner.nextLine();
		scanner.close();
		return tmp;
	}
	
	private void writeMsg(Socket socket, String message) throws IOException {
		int [] messageInt = method.encrypt(message);
		message = method.IntArrayToString(messageInt);
		PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
		printWriter.print(message);
		printWriter.flush();
	}
	
	private void writeRSAMsg(Socket socket, String message) {
		ObjectInputStream inputStream = null;
	      try {
			inputStream = new ObjectInputStream(new FileInputStream(method.PUBLIC_KEY_FILE));
		    final PublicKey publicKey = (PublicKey) inputStream.readObject();
		    final byte[] cipherText = method.encrypt(message, publicKey);
		    System.out.println(cipherText);
//		    ByteArrayOutputStream bOutput = new ByteArrayOutputStream(12);
//		    bOutput.write(cipherText);
		    
			PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
			printWriter.print(cipherText);
			printWriter.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

	}
	
	public String readMsg(Socket socket) throws IOException {
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		char[] buffer = new char[200];
		int amount = bufferedReader.read(buffer, 0, 200);
		String message = new String(buffer, 0, amount);
		return message;
	}
	
	public static void main(String[] args){
		new Client(args);
	}
}