package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

import client.Callback;

public interface ServerIF extends Remote{
	// addition
	public int addition(int first, int second) throws RemoteException;
	
	// subtraction
	public int subtraction(int first, int second) throws RemoteException;
	
	// multiplication
	public int multiplication(int first, int second) throws RemoteException;
	
	// division
	public int division(int first, int second) throws RemoteException;
	
	// radical
	public int square(int first) throws RemoteException;
	
	// power
	public int power (int first, int second) throws RemoteException;
	
	// factorial
	public int factorail(int bound) throws RemoteException;
	
	// deep thought
	public String deepThought(Callback callback) throws RemoteException;
	
	// run deep thought
	public String runDeepThought() throws RemoteException;
}
