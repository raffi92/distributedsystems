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
import encryption.SimpleEncryption;
// TODO add RSA to wrong-parameter-error-message in client and server 
// TODO (optional) client should send a getPublicKey to server (public key needs no encryption) instead of reading server's file. Then use setKey with fetched public key
// TODO (optional) rewrite encrypt decrypt in encryptionIF from int [] to byte [] such that internal representation is also byte array (no need to have two different methods for encrypt and decrpyt in EncryptionIF)

public class ServerEx3 {
	int port = 11111;
	private ServerSocket ssocket;
	private Socket client;
	private String message;
	private String encrypted;
	private EncryptionIF method;

	public ServerEx3(String[] args) throws ClassNotFoundException {
		try {
			ssocket = new ServerSocket(port);
			System.out.println("Waiting for client");
			client = connect();
			System.out.println("connected");
			method = new RSA(EncryptionIF.pathPrefixServer);
			encrypted = readRSAMsg(client);
			//ack
			writeRSAMsg(client, "ACK");
			printAll();
			// select method after key exchange
			if (args.length == 1) {
				if (Integer.parseInt(args[0]) == 0)
					method = new SimpleEncryption();
				if (Integer.parseInt((args[0])) == 1)
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
			} else {
				System.out
						.println("Usage: java client.class [key] [method] Wrong number of parameter. \nArgument 1 must be the key\nArgument 2 must be the selected method\n[method]: \n0 ... SimpleEncryption\n1 ... RC4 Encryption\n");
				System.exit(0);
			}
			if (method == null) {
				System.out.println("Wrong method: \n0 ... SimpleEncryption\n1 ... RC4 Encryption\n");
				System.exit(0);
			}
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
	// communication with symmetric encryption approaches
	private void writeMsg(Socket socket, String message) throws IOException {
		int[] messageInt = method.encrypt(message);
		message = method.IntArrayToString(messageInt);
		PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
		printWriter.print(message);
		printWriter.flush();
	}
	// communication with symmetric encryption approaches
	private String readMsg(Socket socket) throws IOException {
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		char[] buffer = new char[500];
		
		int amount = -1;
		while (amount <= 0){
			amount = bufferedReader.read(buffer, 0, 500);
		}
		String received = new String(buffer, 0, amount);
		message = received;		// for encrypted output in printAll()
		int[] toDecrypt = method.StringToIntArray(received);
		return method.decrypt(toDecrypt);
	}

	private void printAll() {
		System.out.println("++++++++++++++++++++++++++++++++");
		System.out.println("Received message: " + message);
		System.out.println("Decrypted message: " + encrypted);
	}

	public static void main(String[] args) throws ClassNotFoundException {
		new ServerEx3(args);
	}
}
