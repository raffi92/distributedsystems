package networkManagement;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
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
	// shared variables between server and client of one peer
	ArrayList<NodeEntry> table = new ArrayList<NodeEntry>();
	NodeEntry selfNode;
	ServerSocket listener;
	Socket client; 
	
	public Socket connecting(int port, String ip){
		try {
			client = new Socket(ip, port);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return client;
	}
	
	public void disconnection(){
		try {
			//client.close();  // not neccessary
			listener.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setLocalPeerAddress(ServerSocket sock){
		try {
			selfNode = new NodeEntry(InetAddress.getLocalHost().getHostAddress()+":"+sock.getLocalPort());
		} catch (UnknownHostException e2) {
			e2.printStackTrace();
		}
	}
	
	public String getLocalPeerAddress() {
		return selfNode.getIP()+":"+selfNode.getPort();
	}
	
	public void getCurrentState(){
		
	}
	/** 
	 * 
	 * @param ipPort : format will be <ip address>:<port>
	 */
	public void addEntry(String ipPort){
		NodeEntry newNode = new NodeEntry(ipPort);
		table.add(newNode);
	}
	
	public void setClient(Socket client){
		this.client = client;
	}
	
	public void setListener(ServerSocket server){
		listener = server;
	}
	
	public Socket getClient(){
		return client;
	}
	
	public ServerSocket getListener(){
		return listener;
	}
	
	public NodeEntry getSelfNode(){
		return selfNode;
	}
	
}
