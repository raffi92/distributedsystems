package networkManagement;

import java.net.InetAddress;
import java.util.ArrayList;

/* 
 * This Class should contain all utility functions like
 * connecting to network
 * disconnection from network
 * method obtaining the local peer address
 * method listing the current state of the local node
 * (exercise 1.a sheet 4)
 */
public class Management {
	ArrayList<NodeEntry> table = new ArrayList<NodeEntry>();
	
	public void connecting(){
		
	}
	
	public void disconnection(){
		
	}
	
	public void getLocalPeerAddress(){
		
	}
	
	public void getCurrentState(){
		
	}
	
	public void addEntry(InetAddress ip, int port){
		NodeEntry newNode = new NodeEntry(ip, port);
		table.add(newNode);
	}
}
