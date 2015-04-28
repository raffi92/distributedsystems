package server;

import interfaces.Job;
import interfaces.ServerIF;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Server extends UnicastRemoteObject implements ServerIF{
	private static String serverName = "Server";
	private static final long serialVersionUID = 1L;
	static ArrayList<Job<String>> openResults = new ArrayList<>();
	static ArrayList<Future<String>> openFuture = new ArrayList<>();
	static ExecutorService exec = Executors.newCachedThreadPool();
//	private int maxNumOfClients = 5;
//	private int activeClients = 0;
	protected boolean running = true;
	// service to execute jobs
	
	
	public Server() throws RemoteException {
		super();
		// shutdown service
		new Shutdown(this, serverName).start();
		System.out.println("Server started\nEnter 'quit' to exit Server");
		new Thread(new ResultChecker(this)).start();
		
		
	}

	@Override
	public int addition(int first, int second) throws RemoteException {
		return first+second;
	}

	@Override
	public int subtraction(int first, int second) throws RemoteException {
		return first-second;
	}

	@Override
	public int multiplication(int first, int second) throws RemoteException {
		return first*second;
	}

	@Override
	public int factorail(int bound) throws RemoteException {
		int fact = 1;
		for (int i = 1; i <= bound; i++) {
			fact *= i;
		}
		return fact;
	}

	@Override
	public int division(int first, int second) throws RemoteException {
		return (first / second);
	}

	@Override
	public int square(int first) throws RemoteException {
		return first * first;
	}

	@Override
	public int power(int first, int second) throws RemoteException {
		return (int) java.lang.Math.pow(first,second);
	}


	/** instead of manuell command rmiregistry in the console 
	 * registration in main method
	 */
	public static void main(String[] args)
	  {
	    try {
	      LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
	    }
	    
	    catch (RemoteException ex) {
	      System.out.println(ex.getMessage());
	    }
	    try {
	      Naming.rebind(serverName, new Server());
	    }
	    catch (MalformedURLException ex) {
	      System.out.println(ex.getMessage());
	    }
	    catch (RemoteException ex) {
	      System.out.println(ex.getMessage());
	    }
	  }

	@Override
	public Job<String> submit(Callable<String> job) throws RemoteException {
//		activeClients++;
		Future<String> res = exec.submit(job);
		Job<String> ret = new JobImpl<String>();
		openFuture.add(res);
		openResults.add(ret);
		return ret;
	}
	
	protected ArrayList<Future<String>> getFutureList() {
		return openFuture;
	}
	
	protected Job<String> getJob(int index){
		return openResults.get(index);
	}
	
	

}

