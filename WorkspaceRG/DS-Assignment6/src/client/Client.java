package client;

import interfaces.ServerIF;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Scanner;
import java.util.concurrent.Callable;

// TODO client should be asynch
// TODO client should pull or push result
public class Client implements Remote{
	/**
	 * 
	 */
	private static ServerIF server;
	private static String url = "//127.0.0.1/Server";
	private Scanner input;
	private int first;
	private int second;
	private String operation;
	private int result;
	private boolean running;
	
	public Client() throws RemoteException {
		
		input = new Scanner(System.in);
		running = true;
		int scanned = -1;
		while (running) {
			System.out
					.println("Please enter your Operation:\n0...Exit\n1...Addition\n2...Subtraction\n3...Multiplication\n4...Factorial\n5...Division\n6...Square\n7...Power\n8...Submit Job\n");
			while (!input.hasNextInt()){
				input.next();	// waste if input is string
				System.out.println("Enter the number of your operation");
			}
			scanned = input.nextInt();
			switch (scanned) {
			case 1:
				operation = "Addition";
				enterNumbers();
				result = server.addition(first, second);
				printResult();
				break;
			case 2:
				operation = "Substraction";
				enterNumbers();
				result = server.subtraction(first, second);
				printResult();
				break;
			case 3:
				operation = "Multiplication";
				enterNumbers();
				result = server.multiplication(first, second);
				printResult();
				break;
			case 4:
				operation = "Factorial";
				enterNumber();
				result = server.factorail(first);
				printResult();
				break;
			case 5:
				operation = "Division";
				enterNumbers();
				result = server.division(first, second);
				printResult();
				break;
			case 6:
				operation = "Square";
				enterNumber();
				result = server.square(first);
				printResult();
				break;
			case 7:
				operation = "Power";
				enterNumbers();
				result = server.power(first, second);
				printResult();
				break;
			case 8:
				Callable<String> job = new CallableImpl();
				System.out.println(((CallableImpl) job).getFib());
				// TODO return instance of Job
				String jobDone = server.submit(job);
				//jobDone.isDone();
				System.out.println(jobDone);
				break;
			case 0:
				System.out.println("Client shutdown...");
				running = false;
				input.close();
				break;
			default:
				System.out.println("Please try again!");
				break;
			}
		}
	}

	public void enterNumbers() {
		System.out.println("Please enter your first number!\n");
		first = input.nextInt();
		System.out.println("Please enter your second number!\n");
		second = input.nextInt();
	}

	public void enterNumber() {
		System.out.println("Please enter your number!\n");
		first = input.nextInt();
	}

	public String enterQuestion(){
		System.out.println("Please enter your question!\n");
		String input = this.input.next();
		return input;
	}
	public void printResult() {
		System.out.println("The Result of the Operation " + operation + " is "
				+ result + "\n\n\n");
	}
	
	public static int Fibonacci(int n){
	    if (n <= 1)
	        return n;
	    else
	        return Fibonacci(n - 1) + Fibonacci(n - 2);
	}

	public static void main(String[] args) {
		try {
			server = (ServerIF) Naming.lookup(url);
			new Client();
		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			e.printStackTrace();
		}
	}

}
