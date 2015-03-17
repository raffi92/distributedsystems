package protocol;

import java.net.*;
import java.util.Scanner;
import java.io.*;

public class Protocol {
	private static int serverport = 8002;
	private static String master = "Raphael";
	private static int count = 0;
	private static int cache[];
	private static Boolean success = false;
	/**
	 * Client invokes this method
	 * sends Operation and Operators to Server
	 * and gets response
	 */
	public static void request(Socket client){
		try {
			OutputStream sendToServer = client.getOutputStream();
			DataOutputStream out = new DataOutputStream(sendToServer);
			BufferedReader buffer=new BufferedReader(new InputStreamReader(System.in));	
			InputStream receivedFromServer = client.getInputStream();
			DataInputStream in = new DataInputStream(receivedFromServer);
//			while(!success){
//				enterName(out);
//				success = in.readBoolean();
//				if(success != true && count != 3){
//					count++;
//					System.out.println("Wrong master user! Please try again!");
//				}
//				else{
//					System.out.println("You've entered the master user three times wrong! Program shutdown");
//					System.exit(0);
//				}
//			}
//			out.close(); in.close();
			
			while(!checkLogin(enterName(out))){
				count++;
				if(count != 3){
					System.out.println("Wrong master user! Please try again!");			
				}
				else{
					System.out.println("You've entered the master user three times wrong! Program shutdown");
					System.exit(0);
				}
			}
			System.out.println("You've logged in succesfully");
			showOperations();
			for(int i = 0; i < cache.length; i++){
				out.writeInt(cache[i]);
			}
			System.out.println("Result from Server:\n" + in.readInt());
			out.close();
			in.close();
		} catch (IOException e) {
			System.out.println("Something in the client request went wrong");
		}
	}
	

	/**
	 * Server invokes this method
	 * Server gets message from client and replies the result
	 */
	public static void reply(Socket server){
		int service = 0;
		int opt1 = 0;
		int opt2 = 0;
		int result = 0;
		Boolean flag = true;
		try {
		DataInputStream in = new DataInputStream(server.getInputStream());		
		DataOutputStream out = new DataOutputStream(server.getOutputStream());
//		while(!success){
//		out.writeBoolean(checkLogin(in.readUTF()));
//		}
			while(flag){
			service = in.readInt();
			System.out.println(service);
			opt1 = in.readInt();
			System.out.println(opt1);
			opt2 = in.readInt();
			System.out.println(opt2);
			result = getResult(service, opt1, opt2);
			out.writeInt(result);
			//System.out.println(in.readUTF());
			//ask service again out.writeUTF(), client in,out; server in.read if true => flag for while
			flag = false;			
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
	public static int getServerPort(){
		return serverport;
	}

	/**
	 * Server waits till there is an connection with client
	 */
	public static Socket waitForAccept(ServerSocket server) {
		try {
			return server.accept();			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;		
	}
	
	/**
	 * Check if entered user and master user are the same
	 */
	public static Boolean checkLogin(String user){
		if(master.compareTo(user) == 0)
			return true;
		else
			return false;
	}
	
	/**
	 * Enter your name
	 */
	private static String enterName(DataOutputStream out) {
		String user=null;
		System.out.println("\nPlease enter your username!\n");
		//OUT.WRITE verwenden!!!!!
		Scanner in = new Scanner(System.in);	
		BufferedReader buffer=new BufferedReader(new InputStreamReader(System.in));
		try {
			//out.writeUTF(in.next());
			user = buffer.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return user;
	}
	
	/**
	 * First select which service the client prefers
	 * then enters the operators for the service
	 */
	private static void showOperations(){
		int servicenr = 0;
		int num1 = 0;
		int num2 = 0;
		Boolean flag = true;
		Scanner in = new Scanner(System.in);
		System.out.println("Please choose your service!");
		System.out.println("1...Addition\n2...Subtraction\n3...Multiplication\n4...Factorial");
		servicenr = in.nextInt();
		while(flag){
		switch(servicenr){
		case 1:
			System.out.println("Please enter 2 numbers which you want to sum up");
			System.out.println("Number1:");
			num1 = in.nextInt();
			System.out.println("Number2:");
			num2 = in.nextInt();
			fillCache(1,num1,num2);
			flag = false;
			break;
		case 2:
			System.out.println("Please enter 2 numbers which you want to subtract");
			System.out.println("Number1:");
			num1 = in.nextInt();
			System.out.println("Number2:");
			num2 = in.nextInt();
			fillCache(2,num1,num2);
			flag = false;
			break;
		case 3:
			System.out.println("Please enter 2 numbers which you want to multiply");
			System.out.println("Number1:");
			num1 = in.nextInt();
			System.out.println("Number2:");
			num2 = in.nextInt();
			fillCache(3,num1,num2);
			flag = false;
			break;
		case 4:
			System.out.println("Please enter one number of which you prefer the factorial");
			System.out.println("Number:");
			num1 = in.nextInt();
			num2 = 0;
			fillCache(4,num1,num2);
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
	 * @param service
	 * @param op1
	 * @param op2
	 */
	public static void fillCache(int service, int op1, int op2){
		cache = new int[3];
		cache[0] = service;
		cache[1] = op1;
		cache[2] = op2;
	}
	
	/**
	 * Calculates the result of input
	 * 1...Add
	 * 2...Sub
	 * 3...Mul
	 * 4...Fac
	 * @return result
	 */
	public static int getResult(int service, int opt, int opt2){
		int result = 0;
		switch(service){
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
			for(int i = 1; i <= opt2; i++){
				fact *= i;
			}
			result = fact;
			break;
		}
		return result;
	}
}
