package server;

import interfaces.CallbackIF;
import interfaces.ServerIF;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;


// TODO quit server??
public class Server extends UnicastRemoteObject implements ServerIF{

	private static final long serialVersionUID = 1L;
	private CallbackIF call;
	
	protected Server() throws RemoteException {
		super();
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

	@Override
	public String deepThought(CallbackIF callback) throws RemoteException {
		call = callback;
		new Thread(new Runnable(){
			public  void run(){
				try {
					runDeepThought();
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}).start();

		Thread.currentThread().interrupt();
		return null;
	}

	@Override
	public String runDeepThought() throws RemoteException {
		call.getState("Starting DeepThought...");
		try {
			Thread.sleep(5000);
			call.getState("Still running...");
			Thread.sleep(5000);
			call.getState("Please be patient, still running...");
			Thread.sleep(5000);
			call.finish(42);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}

//	public void terminate(){
//		Thread.currentThread().interrupt();
//	}
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
	      Naming.rebind("Server", new Server());
	    }
	    catch (MalformedURLException ex) {
	      System.out.println(ex.getMessage());
	    }
	    catch (RemoteException ex) {
	      System.out.println(ex.getMessage());
	    }
	  }

}
