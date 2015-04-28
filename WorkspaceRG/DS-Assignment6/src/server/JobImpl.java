package server;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import interfaces.Job;



public class JobImpl<T> extends UnicastRemoteObject implements Job<T>, Serializable{
	protected JobImpl() throws RemoteException {
		super();
	}

	private static final long serialVersionUID = 1L;
	private T res;
	private boolean done = false;
	private boolean delivered = false;
	
	@Override
	public boolean isDone() {
		return done;
	}

	@Override
	public T getResult() {
		delivered = true;
		return res;
	}
	
	public void setResult(T res){
		this.res = res;
		done = true;
	}
	
	public boolean deliverd(){
		return delivered;
	}
	
}
