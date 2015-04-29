package dispatcherEx02;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.Callable;

import server.Server;
import interfaces.Dispatcher;
import interfaces.Job;

public class DispatcherImpl extends UnicastRemoteObject implements Dispatcher{
	private static final long serialVersionUID = 1L;
	protected boolean running = true;

	protected DispatcherImpl() throws RemoteException {
		super();
	}

	private LinkedList<Server> serverList = new LinkedList<Server>();
	private Random random = new Random();
	
	@Override
	public void register(Server server) throws RemoteException {
		if(!serverList.contains(server)){
			serverList.add(server);
			System.out.println("Server added to list of servers");
		}
		
	}

	@Override
	public void close(Server server) throws RemoteException {
		if(serverList.contains(server)){
			serverList.remove(server);
			System.out.println("Server has been removed from list of servers");
		}
		
	}

	@Override
	public Server randomServer() throws RemoteException {
		if(serverList.size() == 0){
			System.out.println("Servicelist ist empty");
			return null;
		}
		else
			 return serverList.get(random.nextInt(serverList.size()));	
	}
	
	public Job<String> submit(Callable<String> job) throws RemoteException{
		Server currentServer = randomServer();
		Job<String> tmp = currentServer.submit(job);
		// pick other server if current picked is lazy
		while (tmp == null){
			currentServer = randomServer();
			tmp = currentServer.submit(job);
		}
		return tmp;
	}
	
	public LinkedList<Server> getServerList(){
		return serverList;
	}
	
	public static void testDispatcher(Dispatcher dispatcher){
		String sOne = "server1";
		String sTwo = "server2";
		String sThree = "server3";
		try {
			LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
			Naming.rebind("Dispatcher", dispatcher);
			Server serv1 = new Server(sOne);
			Server serv2 = new Server(sTwo);
			Server serv3 = new Server(sThree);
			dispatcher.register(serv1);
			dispatcher.register(serv2);
			dispatcher.register(serv3);
		} catch (RemoteException | MalformedURLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Start dispatcher
	 * @param args
	 */
	public static void main(String[] args){
		Dispatcher dispatcher = null;
		try {
			dispatcher = new DispatcherImpl();
		} catch (RemoteException e1) {
			e1.printStackTrace();
		}
		testDispatcher(dispatcher);
		new ShutdownDispatcher(dispatcher).start();
	}
	
	public boolean isRunning(){
		return running;
	}
}

