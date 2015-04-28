package clientEx02;

import interfaces.Dispatcher;
import interfaces.Job;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Scanner;
import java.util.concurrent.Callable;

public class Client implements Remote{
	private static Dispatcher dispatcher;
	private static String url = "//127.0.0.1/Dispatcher";
	private Scanner input;
	private String operation;
	private int result;
	private boolean running;
	
	public Client() throws RemoteException {
		try {
			dispatcher = (Dispatcher) Naming.lookup(url);
		} catch (MalformedURLException | NotBoundException e) {
			e.printStackTrace();
		}
		input = new Scanner(System.in);
		running = true;
		int scanned = -1;
		while (running) {
			System.out
					.println("Please enter your Operation:\n0...Exit\n1...Submit Job\n2...Submit Job 2\n");
			while (!input.hasNextInt()){
				input.next();	// waste if input is string
				System.out.println("Enter the number of your operation");
			}
			scanned = input.nextInt();
			switch (scanned) {
			case 1:
				Callable<String> job = new CallableImpl();
				System.out.println(("Input: " + ((CallableImpl) job).getFib()));
				Job<String> jobDone = dispatcher.submit(job);
				new Thread(new PollingService(jobDone, this)).start();
				break;
			case 2: 
				Callable<String> job1 = new CallableImpl();
				System.out.println("Enter input number: ");
				int nr = input.nextInt();
				((CallableImpl) job1).setFib(nr);
				Job<String> jobDone1 = dispatcher.submit(job1);
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
			new Client();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
}
