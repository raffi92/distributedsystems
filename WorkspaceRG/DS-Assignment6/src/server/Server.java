package server;

import interfaces.Job;
import interfaces.ServerIF;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Server extends UnicastRemoteObject implements ServerIF{
	private String serverName = "Server";
	private static final long serialVersionUID = 1L;
	public ArrayList<Job<String>> openResults = new ArrayList<>();
	ArrayList<Future<String>> openFuture = new ArrayList<>();
	// service to execute jobs
	static ExecutorService exec = Executors.newCachedThreadPool();
	protected int maxNumOfJobs = 5;
	protected int activeJobs = 0;
	protected boolean running = true;
	
	
	
	public Server() throws RemoteException {
		super();
		// shutdown service
		new Shutdown(this, serverName).start();
		System.out.println("Server started\nEnter 'quit' to exit Server");
		new Thread(new ResultChecker(this)).start();
	}
	// constructor for dispatcher
	public Server(String name) throws RemoteException {
			super();
			serverName = name;
			new Thread(new ResultChecker(this)).start();
	}

	/** instead of manuell command rmiregistry in the console 
	 * registration in main method
	 */
	public static void main(String[] args)
	  {
	    try {
	      LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
	    }
	    
	    catch (RemoteException ex) {
	      System.out.println(ex.getMessage());
	    }
	    Server s = null;
		try {
			s = new Server();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	    try {
	      Naming.rebind(s.serverName, s);
	    }
	    catch (MalformedURLException | RemoteException ex) {
	      System.out.println(ex.getMessage());
	    }
	  }

	@Override
	public Job<String> submit(Callable<String> job) throws RemoteException {
		activeJobs++;
		System.out.println(getName() + ": Jobs active: " + activeJobs);
		if (activeJobs > maxNumOfJobs){
			activeJobs--;
			System.out.println(getName() + ": Jobs active: " + activeJobs);
			return null;
		}
		else {
			Future<String> res = exec.submit(job);
			Job<String> ret = new JobImpl<String>();
			openFuture.add(res);
			openResults.add(ret);
			return ret;
		}
		
	}
	
	protected ArrayList<Future<String>> getFutureList() {
		return openFuture;
	}
	
	protected Job<String> getJob(int index){
		return openResults.get(index);
	}
	
	public String getName(){
		return serverName;
	}
	
	public ExecutorService getExec(){
		return exec;
	}
	
	public void closeServer(){
		running = false;
	}
	
	

}

