package server;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NoSuchObjectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

public class Shutdown extends Thread{
	Scanner input;
	Boolean running = true;
	Server server = null;
	String name;
	
	public Shutdown (Server server, String name){
		this.server = server;
		this.name = name;
	}
	
	public void run() {
		input = new Scanner(System.in);
		while(running){
			
			if(input.next().equals("quit")){
					// unregister
					try {
						Naming.unbind(name);
					} catch (RemoteException | MalformedURLException | NotBoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					running = false;
					// remove from Rmi runtime
					try {
						UnicastRemoteObject.unexportObject(server, true);
					} catch (NoSuchObjectException e) {
						e.printStackTrace();
					}

			}
		}
		input.close();
	}
}
