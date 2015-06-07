package server;

import java.io.BufferedInputStream;
import org.apache.commons.codec.binary.Base64;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.security.PrivateKey;

import org.omg.CORBA.portable.InputStream;

import encryption.EncryptionIF;
import encryption.RC4;
import encryption.RSA;
import encryption.SimpleEncryption;

public class Server {
	int port = 11111;
	private ServerSocket ssocket;
	private Socket client;
	private String message;
	private String encrypted;
	private EncryptionIF method;
	
	public Server(String [] args) throws ClassNotFoundException{
		try {
			ssocket= new ServerSocket(port);
			System.out.println("Waiting for client");
			client = connect();	
			System.out.println("connected");
			method = null;
			
			if (args.length == 0){
				method = new RSA();
				encrypted = readRSAMsg(client);
			}
			else if (args.length == 2){
				if (Integer.parseInt(args[1]) == 0)
					method = new SimpleEncryption();
				if (Integer.parseInt((args[1])) == 1)
					method = new RC4();
				method.setKey(args[0]);
				encrypted = readMsg(client);
				}
			else {
				System.out.println("Usage: java client.class [key] [method] Wrong number of parameter. \nArgument 1 must be the key\nArgument 2 must be the selected method\n[method]: \n0 ... SimpleEncryption\n1 ... RC4 Encryption\n");
				System.exit(0);
			}
			if (method == null){
				System.out.println("Wrong method: \n0 ... SimpleEncryption\n1 ... RC4 Encryption\n");
				System.exit(0);
			}			

			printAll();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * TODO get byte[] not String from BufferedReader [now: DecryptionError]
	 * TRIED: .getBytes(), Base64decode, ByteArraInputstream(probably working, but not with real socket connect),...
	 * @param client
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private String readRSAMsg(Socket client) throws IOException, ClassNotFoundException {
		ObjectInputStream inputStream = null;
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(client.getInputStream()));
		//int length = Integer.parseInt(bufferedReader.readLine());
		//System.out.println(length);
		//int length = Integer.parseInt(bufferedReader.readLine());
		char[] buffer = new char[500];
		int amount = bufferedReader.read(buffer, 0, 500);
//		BufferedInputStream input = new BufferedInputStream(client.getInputStream());
		byte[] mes = null;
		
		//while (input.read(mes) != -1);
//		ByteArrayInputStream bInput = new ByteArrayInputStream();
//		System.out.println(mes);
//		bInput.read(mes);
		//message = new String(buffer, 0, amount);
		client.getInputStream().read(mes,0,amount);
//		Base64 decoder = new Base64();
//		byte[] cookie = Base64.decodeBase64(message);
//		System.out.println(cookie);
		//mes = message.getBytes(Charset.forName("US-ASCII"));
		//System.out.println(message);
		System.out.println(mes);
	    inputStream = new ObjectInputStream(new FileInputStream(method.PRIVATE_KEY_FILE));
	    final PrivateKey privateKey = (PrivateKey) inputStream.readObject();
		return method.decrypt(mes, privateKey);
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
	
	public static void main(String[] args) throws ClassNotFoundException{
		new Server(args);
	}
}
