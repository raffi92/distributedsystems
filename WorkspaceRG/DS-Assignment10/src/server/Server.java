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
 * Server for exercise 1 and 2: 
 * provide key as parameter at server and client to use symmetric approach (Exercise 1)
 * no parameter internally use RSA approach (Exercise 2)
 */
public class Server {
	int port = 11111;
	private ServerSocket ssocket;
	private Socket client;
	private String message;
	private String encrypted;
	private EncryptionIF method;

	public Server(String[] args) throws ClassNotFoundException {
		try {
			ssocket = new ServerSocket(port);
			System.out.println("Waiting for client");
			client = connect();
			System.out.println("connected");
			method = null;

			if (args.length == 0) {
				method = new RSA(EncryptionIF.pathPrefixServer);
				encrypted = readRSAMsg(client);
				// ack
				writeRSAMsg(client, "ACK");
			} else if (args.length == 1) {
				method = new RC4();
				method.setKey(args[0]);
				encrypted = readMsg(client);
				// ack to client
				writeMsg(client, "ACK");
			} else {
				System.out.println("Usage: java client.class for RSA encryption OR java client.class [key] for RC4 encryption");
				System.exit(0);
			}
			printAll();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

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

	public Socket connect() {
		try {
			return ssocket.accept();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	// communication with symmetric encryption approach
	private void writeMsg(Socket socket, String message) throws IOException {
		int[] messageInt = method.encrypt(message);
		message = method.IntArrayToString(messageInt);
		PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
		printWriter.print(message);
		printWriter.flush();
	}

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
	private String readMsg(Socket socket) throws IOException {
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		char[] buffer = new char[500];
		int amount = bufferedReader.read(buffer, 0, 500);
		message = new String(buffer, 0, amount);
		int[] toDecrypt = method.StringToIntArray(message);
		message = Arrays.toString(toDecrypt); // set for output of print array
		return method.decrypt(toDecrypt);
	}

	private void printAll() {
		System.out.println("++++++++++++++++++++++++++++++++");
		System.out.println("Received message: " + message);
		System.out.println("Decrypted message: " + encrypted);
	}

	public static void main(String[] args) throws ClassNotFoundException {
		new Server(args);
	}
}
