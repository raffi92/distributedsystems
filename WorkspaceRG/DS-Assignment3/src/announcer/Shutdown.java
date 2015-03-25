package announcer;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.Socket;
import java.util.Scanner;

public class Shutdown extends Thread{
	DatagramSocket socket;
	
	public Shutdown (DatagramSocket socket){
		this.socket = socket;
	}
	
	public Shutdown (){
		
	}
	
	public void run() {
		Scanner input = new Scanner(System.in);
		input.next().equals("quit");
		ServiceAnnouncer.socket.close();
		input.close();
	}
}
