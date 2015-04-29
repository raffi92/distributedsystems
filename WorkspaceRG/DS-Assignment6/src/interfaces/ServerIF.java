package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.concurrent.Callable;


public interface ServerIF extends Remote{
	
	// job 
	public Job<String> submit(Callable<String> job) throws RemoteException;
	
}
