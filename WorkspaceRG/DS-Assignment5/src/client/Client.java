package client;

import interfaces.ServerIF;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class Client {
	private static String url = "//127.0.0.1/Server";
	public static void main(String[] args) {
		try {
			ServerIF server = (ServerIF) Naming.lookup(url);
			// user input TODO
			// select method TODO
			int first = 6, second = 6;
			int result = server.factorail(first);
			System.out.println("fact: " + result);
			result = server.addition(first, second);
			System.out.println("addition: " + result);
		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			e.printStackTrace();
		}
	}

}
