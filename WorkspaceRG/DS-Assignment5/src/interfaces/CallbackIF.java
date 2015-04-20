package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface CallbackIF extends Remote{

	public void getState(String state) throws RemoteException;
	
	public void finish(int number) throws RemoteException;
}
