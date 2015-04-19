package server;

import interfaces.ServerIF;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Server extends UnicastRemoteObject implements ServerIF{

	private static final long serialVersionUID = 1L;

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
	// instead of manuell command rmiregistry in the console 
	// registration in main method
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
