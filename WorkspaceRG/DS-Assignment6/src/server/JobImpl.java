package server;

import java.rmi.RemoteException;
import interfaces.Job;



public class JobImpl<T> implements Job<T>{
	private T res;
	private boolean done = false;
	public JobImpl(T res) throws RemoteException{
		this.res = res;
		done = true;
	}
	@Override
	public boolean isDone() {
		return done;
	}

	@Override
	public T getResult() {
		return res;
	}

}
