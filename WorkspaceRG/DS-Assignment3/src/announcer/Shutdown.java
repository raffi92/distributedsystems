package announcer;

import java.net.DatagramSocket;
import java.util.Scanner;

public class Shutdown extends Thread{
	DatagramSocket socket;
	Scanner input;
	Boolean flag = true;
	
	public Shutdown (DatagramSocket socket){
		this.socket = socket;
	}
	
	public Shutdown (){
		
	}
	
	public void run() {
		while(flag){
		input = new Scanner(System.in);
		if(input.next().equals("quit")){
			ServiceAnnouncer.socket.close();
			flag = false;
		}
		}
		input.close();
	}
}
