package server;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.PrivateKey;
import java.util.Arrays;

import encryption.EncryptionIF;
import encryption.RC4;
import encryption.RSA;
import encryption.SimpleEncryption;
// TODO wir haben jeweils ein private und ein public key file. in der angabe steht 4 files. 
// dh. server muss irgendwie public key vom client verwenden oda so. 
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
				method = new RSA();
				encrypted = readRSAMsg(client);
			} else if (args.length == 2) {
				if (Integer.parseInt(args[1]) == 0)
					method = new SimpleEncryption();
				if (Integer.parseInt((args[1])) == 1)
					method = new RC4();
				method.setKey(args[0]);
				encrypted = readMsg(client);
			} else {
				System.out
						.println("Usage: java client.class [key] [method] Wrong number of parameter. \nArgument 1 must be the key\nArgument 2 must be the selected method\n[method]: \n0 ... SimpleEncryption\n1 ... RC4 Encryption\n");
				System.exit(0);
			}
			if (method == null) {
				System.out.println("Wrong method: \n0 ... SimpleEncryption\n1 ... RC4 Encryption\n");
				System.exit(0);
			}

			printAll();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param client
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private String readRSAMsg(Socket client) throws IOException, ClassNotFoundException {
		InputStream in = client.getInputStream();
		DataInputStream datain = new DataInputStream(in);

		int len = datain.readInt();
		byte[] data = new byte[len];
		if (len > 0) {
			datain.readFully(data);
		}
		message = Arrays.toString(data);
		ObjectInputStream objInputStream = new ObjectInputStream(new FileInputStream(EncryptionIF.PRIVATE_KEY_FILE));
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
		int[] toDecrypt = method.StringToIntArray(message);
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
