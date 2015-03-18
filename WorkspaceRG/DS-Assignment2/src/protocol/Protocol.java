package protocol;

import java.net.*;
import java.util.Scanner;
import java.io.*;

public class Protocol {
	// static variables remain the same for all clients
	private static int serverport = 8002;
	private static String master = "master";
	private static int count = 0;

	// client specific variables
	private int cache[];
	private Boolean success = false;
	private Scanner input = new Scanner(System.in); // read from commandline
													// (client)

	/**
	 * Client invokes this method sends Operation and Operators to Server and
	 * gets response
	 */
	public void request(Socket client) {
		try {
			OutputStream sendToServer = client.getOutputStream();
			DataOutputStream out = new DataOutputStream(sendToServer);
			InputStream receivedFromServer = client.getInputStream();
			DataInputStream in = new DataInputStream(receivedFromServer);
			String name = enterName();

			while (!success) {
				out.writeUTF(name);
				success = in.readBoolean();
				if (success != true && count < 2) {
					count++;
					System.out.println("Wrong master user! Please try again!");
					name = enterName();
				} else if (count >= 2) {
					System.out
							.println("You've entered the master user three times wrong! Program shutdown");
					System.exit(0);
				}
			}

			System.out.println("You've logged in succesfully");
			// fetch input
			showOperations();
			// write input to server
			for (int i = 0; i < cache.length; i++) {
				out.writeInt(cache[i]);
			}
			System.out.println("Result from Server:\n" + in.readInt());
			out.close();
			in.close();
			input.close();
		} catch (IOException e) {
			System.out.println("Something in the client request went wrong");
		}
	}

	/**
	 * Server invokes this method Server gets message from client and replies
	 * the result
	 */
	public void reply(Socket server) {
		int service = 0;
		int opt1 = 0;
		int opt2 = 0;
		int result = 0;
		Boolean flag = true;

		try {
			DataInputStream in = new DataInputStream(server.getInputStream());
			DataOutputStream out = new DataOutputStream(
					server.getOutputStream());

			while (!success) {
				try {
					String username = in.readUTF();
					boolean tmp = checkLogin(username);
					out.writeBoolean(tmp);
					success = tmp;
				} catch (EOFException e) {
					System.out.println("Client "
							+ server.getRemoteSocketAddress() + " terminated");
					// set flags to terminate client
					flag = false;
					success = true;
				}

			}
			while (flag) {
				if (in.available() > 0) {
					service = in.readInt();
					opt1 = in.readInt();
					opt2 = in.readInt();
					result = getResult(service, opt1, opt2);
					out.writeInt(result);
					flag = false;
				}
			}
			in.close();
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Returns Server port
	 */
	public static int getServerPort() {
		return serverport;
	}

	/**
	 * Check if entered user and master user are the same
	 */
	public static Boolean checkLogin(String user) {
		if (master.equals(user))
			return true;
		else
			return false;
	}

	/**
	 * Enter your name
	 */
	private String enterName() {
		String user = "";
		System.out.println("\nPlease enter your username!\n");
		while (user.length() == 0)
			user = input.next();

		System.out.println(user);
		return user;
	}

	/**
	 * First select which service the client prefers then enters the operators
	 * for the service
	 */
	private void showOperations() {
		int servicenr = 0;
		int num1 = 0;
		int num2 = 0;
		Boolean flag = true;
		System.out.println("Please choose your service!");
		System.out
				.println("1...Addition\n2...Subtraction\n3...Multiplication\n4...Factorial");

		servicenr = input.nextInt();
		while (flag) {
			switch (servicenr) {
			case 1:
				System.out
						.println("Please enter 2 numbers which you want to sum up");
				System.out.println("Number1:");
				num1 = input.nextInt();
				System.out.println("Number2:");
				num2 = input.nextInt();
				fillCache(1, num1, num2);
				flag = false;
				break;
			case 2:
				System.out
						.println("Please enter 2 numbers which you want to subtract");
				System.out.println("Number1:");
				num1 = input.nextInt();
				System.out.println("Number2:");
				num2 = input.nextInt();
				fillCache(2, num1, num2);
				flag = false;
				break;
			case 3:
				System.out
						.println("Please enter 2 numbers which you want to multiply");
				System.out.println("Number1:");
				num1 = input.nextInt();
				System.out.println("Number2:");
				num2 = input.nextInt();
				fillCache(3, num1, num2);
				flag = false;
				break;
			case 4:
				System.out
						.println("Please enter one number of which you prefer the factorial");
				System.out.println("Number:");
				num1 = input.nextInt();
				num2 = 0;
				fillCache(4, num1, num2);
				flag = false;
				break;
			default:
				System.out.println("Operation not known, try again!");
				break;
			}
		}
	}

	/**
	 * Fill cache with chosen service and binary or unary operations
	 * 
	 * @param service
	 * @param op1
	 * @param op2
	 */
	public void fillCache(int service, int op1, int op2) {
		cache = new int[3];
		cache[0] = service;
		cache[1] = op1;
		cache[2] = op2;
	}

	/**
	 * Calculates the result of input 1...Add 2...Sub 3...Mul 4...Fac
	 * 
	 * @return result
	 */
	public int getResult(int service, int opt, int opt2) {
		int result = 0;
		switch (service) {
		case 1:
			result = opt + opt2;
			break;
		case 2:
			result = opt - opt2;
			break;
		case 3:
			result = opt * opt2;
			break;
		case 4:
			int fact = 1;
			for (int i = 1; i <= opt; i++) {
				fact *= i;
			}
			result = fact;
			break;
		}
		return result;
	}
}
