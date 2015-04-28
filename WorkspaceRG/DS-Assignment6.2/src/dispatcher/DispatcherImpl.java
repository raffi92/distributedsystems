package dispatcher;

import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.Callable;

import server.Server;
import interfaces.Dispatcher;

//TODO register Servers in DispatcherImpl
//TODO registry/main for dispatcher
//TODO client calls dispatcher
//TODO adapt all operations or delete them (except Fibonacci)
//TODO c) theory question

public class DispatcherImpl implements Dispatcher{
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
	
	public String submit(Callable<String> job) throws RemoteException{
		Server currentServer = randomServer();
		return currentServer.submit(job);
	}

}
