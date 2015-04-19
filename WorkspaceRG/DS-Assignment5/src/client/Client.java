package client;

import interfaces.ServerIF;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Scanner;

public class Client {
	private static ServerIF server;
	private static String url = "//127.0.0.1/Server";
	private Scanner input;
	private int method;
	private int first;
	private int second;
	private String operation;
	private int result;
	
	public Client() throws RemoteException{
		input = new Scanner(System.in);
		System.out.println("Please enter your Operation:\n1...Addition\n2...Subtraction\n3...Multiplication\n4...Factorial\n");
		switch(method = input.nextInt()){
			case 1:
				operation = "Addition";
				enterNumbers();
				result = server.addition(first,second);
				printResult();
				break;
			case 2:
				operation = "Substraction";
				enterNumbers();
				result = server.subtraction(first, second);
				printResult();
				break;
			case 3:
				operation = "Multiplication";
				enterNumbers();
				result = server.multiplication(first, second);
				printResult();
				break;
			case 4:
				operation = "Factorial";
				enterNumber();
				result = server.factorail(first);
				printResult();
				break;
		}
	}
	
	public void enterNumbers(){
		System.out.println("Please enter your first number!\n");
		first = input.nextInt();
		System.out.println("Please enter your second number!\n");
		second = input.nextInt();
	}
	
	public void enterNumber(){
		System.out.println("Please enter your number!\n");
		first = input.nextInt();
	}
	
	public void printResult(){
		System.out.println("The Result of the Operation " + operation + " is " + result);
	}
	
	public static void main(String[] args) {
			try {
				server = (ServerIF) Naming.lookup(url);
				Client client = new Client();
			} catch (MalformedURLException | RemoteException
					| NotBoundException e) {
				e.printStackTrace();
			}
	}

}
