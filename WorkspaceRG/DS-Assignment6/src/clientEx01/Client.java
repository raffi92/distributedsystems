package clientEx01;

import interfaces.Job;
import interfaces.ServerIF;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Scanner;
import java.util.concurrent.Callable;

// TODO client should be asynch
// TODO polling service
public class Client implements Remote{
	private static ServerIF server;
	private static String url = "//127.0.0.1/Server";
	private Scanner input;
	private int first;
	private String operation;
	private int result;
	private boolean running;
	
	public Client() throws RemoteException {
		
		input = new Scanner(System.in);
		running = true;
		int scanned = -1;
		while (running) {
			System.out
					.println("Please enter your Operation:\n0...Exit1...Factorial\n2...Submit Job(fib 10)\n3...Submit Job (fib dynamic)\n");
			while (!input.hasNextInt()){
				input.next();	// waste if input is string
				System.out.println("Enter the number of your operation");
			}
			scanned = input.nextInt();
			switch (scanned) {
			case 1:
				operation = "Factorial";
				enterNumber();
				result = server.factorail(first);
				printResult();
				break;
			case 2:
				Callable<String> job = new CallableImpl();
				System.out.println(("Input: " + ((CallableImpl) job).getFib()));
				Job<String> jobDone = server.submit(job);
				new Thread(new PollingService(jobDone, this)).start();
				break;
			case 3: 
				Callable<String> job1 = new CallableImpl();
				System.out.println("Enter input number: ");
				int nr = input.nextInt();
				((CallableImpl) job1).setFib(nr);
				Job<String> jobDone1 = server.submit(job1);
				new Thread(new PollingService(jobDone1, this)).start();
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
	
	public void deliverResult(String result2) {
		System.out.println(result2);
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
