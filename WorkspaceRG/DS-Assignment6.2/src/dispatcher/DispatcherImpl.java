package dispatcher;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RemoteObject;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.Callable;

import server.Server;
import interfaces.Dispatcher;

//TODO client calls dispatcher
//TODO adapt all operations or delete them (except Fibonacci)
//TODO c) theory question
//TODO single server shutdown by name?

public class DispatcherImpl implements Dispatcher{
	private LinkedList<Server> serverList = new LinkedList<Server>();
	private Random random = new Random();
	private static Dispatcher dispatch;
	
	public DispatcherImpl(){
		testDispatcher();
	}
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
	
	public String submit(Callable<String> job) throws RemoteException{
		Server currentServer = randomServer();
		return currentServer.submit(job);
	}
	
	public void testDispatcher(){
		String sOne = "server1";
		String sTwo = "server2";
		String sThree = "server3";
		try {
			LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
//			Naming.rebind(sOne, new Server (sOne));
//			Naming.rebind(sTwo, new Server(sTwo));
//			Naming.rebind(sThree, new Server(sThree));
			Server serv1 = new Server(sOne);
			Server serv2 = new Server(sTwo);
			Server serv3 = new Server(sThree);
//			final Server obj1 = (Server) UnicastRemoteObject.exportObject(serv1, 0);
//			final Server obj2 = (Server) UnicastRemoteObject.exportObject(serv2,0);
//			final Server obj3 = (Server) UnicastRemoteObject.exportObject(serv3, 0);
			register(serv1);
			register(serv2);
			register(serv3);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Start dispatcher
	 * @param args
	 */
	public static void main(String[] args){
		new DispatcherImpl();
		
	}

}
