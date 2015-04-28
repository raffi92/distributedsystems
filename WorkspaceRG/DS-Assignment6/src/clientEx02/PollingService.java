package clientEx02;

import java.rmi.RemoteException;

import interfaces.Job;

public class PollingService implements Runnable{
	Job<String> open;
	Client client;
	public PollingService(Job<String> job, Client client){
		open = job;
		this.client = client;
	}
	@Override
	public void run() {
		try {
			while(!open.isDone()){
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			if(open.isDone()){
				System.out.println("isDone");
				client.deliverResult(open.getResult());
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
}
