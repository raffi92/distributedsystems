package server;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;
import encryption.EncryptionIF;
import encryption.RC4;
import encryption.RSA;
/**
 * Hybrid approach uses symmetric approach from exercise 1 and RSA implementation of exercise 2
 * Simulate system with ssl based message exchange
 */
public class ServerHybrid {
	int port = 11111;
	private ServerSocket ssocket;
	private Socket client;
	private String message;
	private String encrypted;
	private EncryptionIF method;

	public ServerHybrid(String[] args) throws ClassNotFoundException {
		try {
			// exchange key
			ssocket = new ServerSocket(port);
			System.out.println("Waiting for client");
			client = connect();
			System.out.println("connected");
			method = new RSA(EncryptionIF.pathPrefixServer);
			// read key
			encrypted = readRSAMsg(client);
			// send ack
			writeRSAMsg(client, "ACK");
			printAll();
			handleCommunication();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Socket connect() {
		try {
			return ssocket.accept();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	// communication with RSA approach
	private String readRSAMsg(Socket client) throws IOException, ClassNotFoundException {
		InputStream in = client.getInputStream();
		DataInputStream datain = new DataInputStream(in);
		int len = datain.readInt();
		byte[] data = new byte[len];
		if (len > 0) {
			datain.readFully(data);
		}
		message = Arrays.toString(data);
		// use own private key to decrypt data
		ObjectInputStream objInputStream = new ObjectInputStream(new FileInputStream(EncryptionIF.pathPrefixServer + EncryptionIF.PRIVATE_KEY_FILE));
		final PrivateKey privateKey = (PrivateKey) objInputStream.readObject();
		objInputStream.close();
		return method.decrypt(data, privateKey);
	}

	// communication with RSA approach
	private void writeRSAMsg(Socket socket, String message) {
		ObjectInputStream inputStream = null;
		try {
			// use public key of server
			inputStream = new ObjectInputStream(new FileInputStream(EncryptionIF.pathPrefixClient + EncryptionIF.PUBLIC_KEY_FILE));
			final PublicKey publicKey = (PublicKey) inputStream.readObject();
			final byte[] cipherText = method.encrypt(message, publicKey);
			OutputStream out = socket.getOutputStream();
			DataOutputStream dataout = new DataOutputStream(out);
			dataout.writeInt(cipherText.length);
			dataout.write(cipherText, 0, cipherText.length);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

	}

	// communication with symmetric encryption approach
	private void writeMsg(Socket socket, String message) throws IOException {
		int[] messageInt = method.encrypt(message);
		message = method.IntArrayToString(messageInt);
		PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
		printWriter.print(message);
		printWriter.flush();
	}

	// communication with symmetric encryption approach
	private String readMsg(Socket socket) throws IOException {
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		char[] buffer = new char[500];
		int amount = bufferedReader.read(buffer, 0, 500);
		String received = new String(buffer, 0, amount);
		String tmp = new String(buffer, 0, received.length()); // for encrypted output in printAll()
		message = Arrays.toString(method.StringToIntArray(tmp)); // for encrypted output in printAll()
		int[] toDecrypt = method.StringToIntArray(received);
		return method.decrypt(toDecrypt);
	}

	private void printAll() {
		System.out.println("++++++++++++Server++++++++++++");
		System.out.println("Received message: " + message);
		System.out.println("Decrypted message: " + encrypted);
	}
	// manage communication
	private void handleCommunication() throws IOException{
		method = new RC4();
		// set key for symmetric encryption
		method.setKey(encrypted);
		// receive first
		encrypted = readMsg(client);
		printAll();
		System.out.println("Client send: ACK");
		writeMsg(client, "ACK");
		// receice second
		encrypted = readMsg(client);
		printAll();
		System.out.println("Client send: ACK");
		writeMsg(client, "ACK");
	}
	
	public static void main(String[] args) throws ClassNotFoundException {
		new ServerHybrid(args);
	}
}
