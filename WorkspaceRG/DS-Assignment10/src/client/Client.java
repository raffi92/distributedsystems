package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
		private String ip = "127.0.0.1"; // localhost
		private int port = 11111;
		private Socket socket;
		private int key;
		
		public Client(String[] args){
			try {
				if (args.length != 1){
					System.out.println("Wrong number of parameter. Argument 0 must be the key");
					System.exit(0);
				}
				setKey(args[0]);
				socket = new Socket(ip,port);
				String message = enterMessage();
				writeMsg(socket,message);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	public String enterMessage(){
		Scanner scanner = new Scanner(System.in); 
		System.out.println("Please enter your message");
		return scanner.next();
	}
	
	private void writeMsg(Socket socket, String message) throws IOException {
		message = encode(message);
		PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
		printWriter.print(message);
		printWriter.flush();
	}
	
	public String readMsg(Socket socket) throws IOException {
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		char[] buffer = new char[200];
		int amount = bufferedReader.read(buffer, 0, 200);
		String message = new String(buffer, 0, amount);
		return message;
	}
	
	private String encode(String in)
	{
		String out = "";
		for (int i = 0; i < in.length(); i++) {
			char ch = in.charAt(i);
			ch = (char) (' ' + ((ch - ' ' + key) % 98));
			out += ch;
		}
		String keyString = Integer.toString(key);
		int length = keyString.length();
		out = out + keyString + '+' + length;
		System.out.println(out);
		return out;
	}

	
	private void setKey(String arg){
		key = Integer.parseInt(arg);
	}
	
	public static void main(String[] args){
		Client client = new Client(args);
	}

/**
 * RSA
 * 
 * try {
      // zufaelligen Schluessel erzeugen
      KeyPairGenerator keygen = KeyPairGenerator.getInstance("RSA");
      keygen.initialize(1024);
      KeyPair rsaKeys = keygen.genKeyPair();
       
      // Klasse erzeugen
      EasyCrypt ecPri = new EasyCrypt(rsaKeys.getPrivate(), "RSA");
      EasyCrypt ecPub = new EasyCrypt(rsaKeys.getPublic(), "RSA");
       
      // Text ver- und entschluesseln
      String text = "Hallo AxxG-Leser";
      String geheim = ecPri.encrypt(text);
      String erg = ecPub.decrypt(geheim);
       
      System.out.println("Normaler Text:" + text);
      System.out.println("Geheimer Text:" + geheim);
      System.out.println("decrypt  Text:" + erg);      
       
      // oder 
       
      text = "Hallo AxxG-Leser";
      geheim = ecPub.encrypt(text);
      erg = ecPri.decrypt(geheim);
       
      System.out.println("Normaler Text:" + text);
      System.out.println("Geheimer Text:" + geheim);
      System.out.println("decrypt  Text:" + erg);         
   } catch (Exception e) {
      e.printStackTrace();
   }*/
	}
