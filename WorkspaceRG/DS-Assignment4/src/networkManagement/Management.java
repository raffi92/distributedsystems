package networkManagement;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Iterator;

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
	Thread pushService;
	int maxNumberOfNodes = 5;
	
	public Socket connecting(int port, String ip){
		try {
			client = new Socket(ip, port);
		} catch (IOException e) {
			if (e.getMessage().equals("Connection refused")){
				removeEntry(ip, port);
			}
			else {
				e.printStackTrace();
			}
		}
		return client;
	}
	
	// TODO eventuell unterscheidung zwischen quit und disconnect???
	public void disconnection(){
		try {
			//client.close();  // not neccessary
			listener.close();
			pushService.interrupt();
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
		printTable();
	}
	// used in pushService
	public void shareNodes(){
		// select node one node in table
		NodeEntry selected = randomNode();
		// no entries to share
		if (selected == null){
			return;
		}
		// establish connection to this node
		Socket sharingSocket = connecting(selected.getPort(), selected.getIP());
		// create streams
		try {
			DataOutputStream out = new DataOutputStream(sharingSocket.getOutputStream());
			DataInputStream in = new DataInputStream(sharingSocket.getInputStream());
			String tableString = getStringOfTableEntries();
			// send all node entries to this node
			out.writeUTF(tableString);
			if(in.readBoolean()){
				System.out.print(".");
			}
		} catch (IOException e) {
			if (!e.getMessage().equals("Socket is closed"))
				e.printStackTrace();
		}
		// close
		try {
			sharingSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/** 
	 * 
	 * @param ipPort : format will be <ip address>:<port>
	 */
	public void addEntry(String ipPort){
		// multiple ipPort pairs
		if (ipPort.split("#").length > 0){
			String [] ipPorts = ipPort.split("#");
			for (int i = 0; i < ipPorts.length; i++){
				NodeEntry newNode = new NodeEntry(ipPorts[i]);
				table.add(newNode);
			}
		} else {
			// single node
			NodeEntry newNode = new NodeEntry(ipPort);
			table.add(newNode);
		}
		// delete invalid nodes
		deleteSelfRef();
		deleteDoubleRef();
		
		// limit of node entries in table
		while(table.size()>maxNumberOfNodes){
			int rand = new Random().nextInt(table.size());
			table.remove(rand);
		}
	}
	
	// remove entry from table (node is offline)
	public void removeEntry(String ip, int port){
		Iterator<NodeEntry> iter = table.iterator();
		while(iter.hasNext()){
			NodeEntry tmp = iter.next();
			if(tmp.getIP().equals(ip) && tmp.getPort() == port)
				iter.remove();
		}
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
	
	public void setPushServiceThread(Thread push){
		pushService = push;
	}
	
	public NodeEntry getSelfNode(){
		return selfNode;
	}
	
	public void printTable(){
		for (int i = 0; i < table.size(); i++) {
			System.out.println((i+1) + ")" + table.get(i).getIP() + ":" + table.get(i).getPort());
		}
		if (table.size() == 0){
			System.out.println("no entries available");
		}
	}
	
	public boolean isRunning(){
		return !listener.isClosed();
	}
	
	private NodeEntry randomNode(){
		int range = table.size();
		if (range == 0)
			return null;
		else {
			int randIndex = new Random().nextInt(range);
			return table.get(randIndex);
		}
			
	}
	// build format: <ip>:<port>#<ip>:<port> ... 
	private String getStringOfTableEntries(){
		String tableString = "";
		for (int i = 0; i < table.size(); i++) {
			tableString = tableString +table.get(i).getIP() + ":" + table.get(i).getPort() + "#";
		}
		return tableString;
	}
	// delete self reference
	private void deleteSelfRef(){
		Iterator<NodeEntry> iter = table.iterator();
		while(iter.hasNext()){
			NodeEntry tmp = iter.next();
			if (tmp.getIP().equals(selfNode.getIP()) && tmp.getPort() == selfNode.getPort()){
				iter.remove();
			}
		}
	}
	// delete duplicated entries
	private void deleteDoubleRef(){
		for (int i = 0; i < table.size(); i++){
			for (int j = i + 1; j < table.size(); j++){
				if(table.get(i).getIP().equals(table.get(j).getIP()) && table.get(i).getPort() == table.get(j).getPort()){
					table.remove(j);
				}
			}
		}
	}
	
}
