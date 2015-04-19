package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerIF extends Remote{
	// addition
	public int addition(int first, int second) throws RemoteException;
	
	// subtraction
	public int subtraction(int first, int second) throws RemoteException;
	
	// multiplication
	public int multiplication(int first, int second) throws RemoteException;
	
	// factorial
	public int factorail(int bound) throws RemoteException;
}
