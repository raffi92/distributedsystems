package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Job<T> extends Remote{
	public boolean isDone() throws RemoteException;
	public T getResult() throws RemoteException;
}
