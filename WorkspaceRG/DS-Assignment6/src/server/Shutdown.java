package server;

import interfaces.Job;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NoSuchObjectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Scanner;

public class Shutdown extends Thread{
	Scanner input;
	Server server = null;
	String name;
	
	public Shutdown (Server server, String name){
		this.server = server;
		this.name = name;
	}
	
	public void run() {
		input = new Scanner(System.in);
		while(server.running){
			String option = input.next();
			if(option.equals("quit") || option.equals("0")){
					// unregister
					try {
						Naming.unbind(name);
					} catch (RemoteException | MalformedURLException | NotBoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					server.running = false;
					// remove server from Rmi runtime
					try {
						UnicastRemoteObject.unexportObject(server, true);
					} catch (NoSuchObjectException e) {
						e.printStackTrace();
					}
					
					// unregister all Jobs
					ArrayList<Job<String>> tmp = server.openResults;
					for (Job<String> t : tmp){
						try {
							UnicastRemoteObject.unexportObject(t, true);
						} catch (NoSuchObjectException e) {
							e.printStackTrace();
						}
					}
					// quit executor service
					Server.exec.shutdown();
					
			}
		}
		input.close();
		System.out.println("shutdown");
	}
}
