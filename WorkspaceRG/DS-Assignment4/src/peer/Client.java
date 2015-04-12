package peer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import networkManagement.Management;
import networkManagement.NodeEntry;


public class Client implements Runnable {
	private int initPort = 0;
	private String initIp = null;
	private Socket client;
	private Management manager;

	public Client(String initIp, int initPort, Management manager) {
		this.initPort = initPort;
		this.initIp = initIp;
		this.manager = manager;
	}

	@Override
	public void run() {
		// initport zero denotes new network
		if (initPort != 0) {
			try {
				// create socket
				client = manager.connecting(initPort, initIp);
				// add init node to table
				manager.addEntry(new NodeEntry(initIp, initPort));
				// create input and output streams
				OutputStream sendToServer = client.getOutputStream();
				DataOutputStream out = new DataOutputStream(sendToServer);
				InputStream receivedFromServer = client.getInputStream();
				DataInputStream in = new DataInputStream(receivedFromServer);
				// send own node to other peer
				JSONObject message = manager.buildJsonObjectOfTable();
				out.writeUTF(message.toString());
				// get response with table of init node
				JSONObject answer = new JSONObject(in.readUTF());
				JSONArray tableArray = (JSONArray) answer.get("table");
				for (int i = 0;i<tableArray.length();i++){
					JSONObject tmp = (JSONObject) tableArray.get(i);
					NodeEntry tmp2 = new NodeEntry(tmp.getString("IP"), tmp.getInt("port"));
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
		} else {
			System.out.println("new network created");
		}
		
	}

}
