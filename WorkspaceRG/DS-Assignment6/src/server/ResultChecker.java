package server;

import java.rmi.NoSuchObjectException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class ResultChecker implements Runnable{
	Server server = null;
	
	public ResultChecker(Server server) {
		this.server = server;
		System.out.println("result checker started");
	}
	@Override
	public void run() {
		while(server.running){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			ArrayList<Future<String>> tmp = Server.openFuture;
			for(int i = 0; i < tmp.size(); i++){
				if (tmp.get(i).isDone()){
					JobImpl<String> job = (JobImpl<String>) Server.openResults.get(i);
					if (job.deliverd()){
						try {
							UnicastRemoteObject.unexportObject(job, true);
						} catch (NoSuchObjectException e) {
							e.printStackTrace();
						}
						Server.openResults.remove(i);
						Server.openFuture.remove(i);
						System.out.println("removed");
					} else {
						try {
							// set result if future is finished
							job.setResult(tmp.get(i).get());
						} catch (InterruptedException | ExecutionException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		System.out.println("result checker shutdown");
	}
}
