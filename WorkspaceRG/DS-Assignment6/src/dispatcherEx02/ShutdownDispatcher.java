package dispatcherEx02;

import interfaces.Dispatcher;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NoSuchObjectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;
import java.util.Scanner;

import server.Server;

public class ShutdownDispatcher extends Thread{
	DispatcherImpl dis;
	
	public ShutdownDispatcher (Dispatcher dis){
		this.dis = (DispatcherImpl) dis;
	}
	
	@SuppressWarnings("unchecked")
	public void run() {
		Scanner input = new Scanner(System.in);
		while(dis.isRunning()){
			String option = null;
			option = input.next();
			if(option.equals("quit") || option.equals("0")){
				for (Server s : (LinkedList<Server>) dis.getServerList().clone()){
					// remove server
					try {
						dis.close(s);
					} catch (RemoteException e3) {
						e3.printStackTrace();
					}
					// quit Threads of Server
					s.closeServer();
					
					// remove server from Rmi runtime
					try {
						UnicastRemoteObject.unexportObject(s, true);
					} catch (NoSuchObjectException e1) {
						e1.printStackTrace();
					}
					s.getExec().shutdownNow();
				}
				dis.running = false;
				// unbind dispatcher
				try {
					Naming.unbind("Dispatcher");
				} catch (RemoteException | MalformedURLException | NotBoundException e) {
					e.printStackTrace();
				}
				
				// remove server from Rmi runtime
				try {
					UnicastRemoteObject.unexportObject(dis, true);
				} catch (NoSuchObjectException e) {
					e.printStackTrace();
				}
				
			}
		}
		input.close();
		System.out.println("DISPATCHER shutdown");
	}
}
