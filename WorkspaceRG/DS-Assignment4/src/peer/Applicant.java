package peer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import networkManagement.Management;
import networkManagement.NodeEntry;

// send init address and receive first table
// register peer in the network
public class Applicant implements Runnable {
	private int initPort = 0;
	private String initIp = null;
	private Socket client;
	private Management manager;
	private boolean started = false;
	private Scanner inputScanner = null;

	public Applicant(String initIp, int initPort, Management manager, Scanner inputScanner) {
		this.initPort = initPort;
		this.initIp = initIp;
		this.manager = manager;
		this.inputScanner = inputScanner;
	}

	@Override
	public void run() {
		// initport zero denotes new network
		if (initPort != 0) {
			try {
				// create socket
				client = manager.initConnecting(initPort, initIp);
				// wrong input
				while (client == null){
					readInit();
					client = manager.initConnecting(initPort, initIp);
				}
				started = true;
				// create input and output streams
				DataOutputStream out = new DataOutputStream(client.getOutputStream());
				DataInputStream in = new DataInputStream(client.getInputStream());
				// send own node to other peer
				JSONObject message = manager.buildJsonObjectOfTable();
				out.writeUTF(message.toString());
				// get response with table of init node
				JSONObject answer = new JSONObject(in.readUTF());
				JSONArray tableArray = (JSONArray) answer.get("table");
				for (int i = 0;i<tableArray.length();i++){
					JSONObject tmp = (JSONObject) tableArray.get(i);
					NodeEntry tmp2 = new NodeEntry(tmp.getString("IP"), tmp.getInt("port"), tmp.getString("name"));
					manager.addEntry(tmp2);
				}
				manager.deleteInvalid();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			// disconnect
			manager.disconnecting(client);
		} else {
			System.out.println("new network created");
			started = true;
		}
		
	}
	
	public boolean started(){
		return started;
	}
	
	public void readInit(){
		System.out.println("Enter IP");
		initIp = inputScanner.nextLine();
		if (initIp.equals("0")) // new network
			initPort = 0;
		else if (initIp.contains(":")){
			// <ip>:<port> format
			initPort = Integer.parseInt(initIp.split(":")[1]);
			initIp = initIp.split(":")[0];
		} else { // port separately entered
			System.out.println("Enter port");
			initPort = Integer.parseInt(inputScanner.nextLine());
		}
	}

}
