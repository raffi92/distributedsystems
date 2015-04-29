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

public class Client implements Remote{
	private static ServerIF server;
	private static String url = "//127.0.0.1/Server";
	private Scanner input;
	private boolean running;
	
	public Client() throws RemoteException {
		
		input = new Scanner(System.in);
		running = true;
		int scanned = -1;
		while (running) {
			System.out
					.println("Please enter your Operation:\n0...Exit\n1...Submit Job(fib 10)\n2...Submit Job (fib custom)\n");
			while (!input.hasNextInt()){
				input.next();	// waste if input is string
				System.out.println("Enter the number of your operation");
			}
			scanned = input.nextInt();
			switch (scanned) {
			case 1:
				Callable<String> job = new CallableImpl();
				System.out.println(("Input: " + ((CallableImpl) job).getFib()));
				Job<String> jobDone = server.submit(job);
				if (jobDone != null){
					new Thread(new PollingService(jobDone, this)).start();
					System.out.println("Job accepted");
				} else {
					System.out.println("Job refused");
				}
				break;
			case 2: 
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

	public String enterQuestion(){
		System.out.println("Please enter your question!\n");
		String input = this.input.next();
		return input;
	}
	
	public void printResult(String result2) {
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
