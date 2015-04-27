package server;

import interfaces.ServerIF;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Server extends UnicastRemoteObject implements ServerIF{
	private static String serverName = "Server";
	private static final long serialVersionUID = 1L;
	// service to execute jobs
	ExecutorService exec = Executors.newCachedThreadPool();
	
	protected Server() throws RemoteException {
		super();
		// shutdown service
		new Shutdown(this, serverName).start();
		System.out.println("Server started\nEnter 'quit' to exit Server");
		
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
	// TODO change return type, also in interface
	public String submit(Callable<String> job) throws RemoteException {
		Future<String> res = exec.submit(job);
		String resu = null;
		try {
			resu = (String) res.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		//Job<T> ret = new JobImpl(resu);
		
		return "result from server: " + resu;
	}

}
