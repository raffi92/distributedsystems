package client;

import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import interfaces.CallbackIF;

public class Callback extends UnicastRemoteObject implements CallbackIF {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String question;
	
	public Callback(String question) throws RemoteException{
		super();
		this.question = question;
	}
	
	public void getState(String state){
		System.out.println(state);
	}
	
	public void finish(int number){
		System.out.println("The anwser to your question: " + question + " - is " + number);
	}
}
