package client;

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
import java.net.Socket;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.Scanner;

import encryption.EncryptionIF;
import encryption.RC4;
import encryption.RSA;
/**
 * Hybrid approach uses symmetric approach from exercise 1 and RSA implementation of exercise 2
 * Simulate system with ssl based message exchange
 */
public class ClientHybrid {
	private String ip = "127.0.0.1"; // localhost
	private int port = 11111;
	private Socket socket;
	private EncryptionIF method;
	private String response;
	private String decrypted;
	private String message;

	public ClientHybrid(String[] args) {
		try {
			if (args.length != 1) {
				System.out.println("Usage: java client.class [key]");
				System.exit(0);
			}
			socket = new Socket(ip, port);
			method = new RSA(EncryptionIF.pathPrefixClient);
			// rsa message is key for symmetric encryption
			message = args[0];
			writeRSAMsg(socket, message);
			decrypted = readRSAMsg(socket);
			printAll();
			method = new RC4();
			method.setKey(args[0]);
			handleCommunication();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String enterMessage() {
		Scanner scanner = new Scanner(System.in);
		System.out.println("Please enter your message");
		String tmp = scanner.nextLine();
		scanner.close();
		return tmp;
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
		String response = new String(buffer, 0, amount);
		this.response = Arrays.toString(method.StringToIntArray(response)); // set response for encrypted output (printAll)
		int[] toDecrypt = method.StringToIntArray(response);
		return method.decrypt(toDecrypt);
	}

	// communication with RSA approach
	private void writeRSAMsg(Socket socket, String message) {
		ObjectInputStream inputStream = null;
		try {
			// use public key of server
			inputStream = new ObjectInputStream(new FileInputStream(EncryptionIF.pathPrefixServer + EncryptionIF.PUBLIC_KEY_FILE));
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

	// communication with RSA approach
	private String readRSAMsg(Socket client) throws IOException, ClassNotFoundException {
		InputStream in = client.getInputStream();
		DataInputStream datain = new DataInputStream(in);
		int len = datain.readInt();
		byte[] data = new byte[len];
		if (len > 0) {
			datain.readFully(data);
		}
		response = Arrays.toString(data);	// set response for encrypted output (printAll)
		// use own private key to decrypt data
		ObjectInputStream objInputStream = new ObjectInputStream(new FileInputStream(EncryptionIF.pathPrefixClient + EncryptionIF.PRIVATE_KEY_FILE));
		final PrivateKey privateKey = (PrivateKey) objInputStream.readObject();
		objInputStream.close();
		return method.decrypt(data, privateKey);
	}

	private void printAll() {
		System.out.println("++++++++++++Client++++++++++++");
		System.out.println("Client send message: " + message);
		System.out.println("Received message: " + response);
		System.out.println("Decrypted message: " + decrypted);
	}
	// all communication from client
	private void handleCommunication() throws IOException {
		// send first
		message = "hallo";
		writeMsg(socket, message);
		decrypted = readMsg(socket);
		printAll();
		// send second
		message = "foo bar";
		writeMsg(socket, message);
		decrypted = readMsg(socket);
		printAll();
	}

	public static void main(String[] args) {
		new ClientHybrid(args);
	}
}