package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.concurrent.Callable;


public interface ServerIF extends Remote{
	// factorial
	public int factorail(int bound) throws RemoteException;
	
	// job 
	public Job<String> submit(Callable<String> job) throws RemoteException;
	
}
