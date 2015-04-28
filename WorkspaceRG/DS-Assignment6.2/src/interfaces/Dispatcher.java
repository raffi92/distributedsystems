package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.concurrent.Callable;

import server.Server;

public interface Dispatcher extends Remote{

	//register server
	public void register(Server server) throws RemoteException;
	
	//unregister server
	public void close(Server server) throws RemoteException;
	
	//choose random server
	public Server randomServer() throws RemoteException;
}
