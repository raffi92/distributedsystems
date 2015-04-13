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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* 
 * This Class contain all utility functions like
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

	public Socket connecting(int port, String ip) {
		try {
			client = new Socket(ip, port);
		} catch (IOException e) {
			if (e.getMessage().equals("Connection refused")) {
				removeEntry(ip, port);
				shareNodes();
			} else {
				e.printStackTrace();
			}
		}
		return client;
	}

	// TODO eventuell unterscheidung zwischen quit und disconnect???
	public void disconnection() {
		try {
			// client.close(); // not neccessary
			listener.close();
			pushService.interrupt();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setLocalPeerAddress(ServerSocket sock, String name) {
		try {
			selfNode = new NodeEntry(InetAddress.getLocalHost().getHostAddress() + ":" + sock.getLocalPort(), name);
		} catch (UnknownHostException e2) {
			e2.printStackTrace();
		}
	}

	public String getLocalPeerAddress() {
		return selfNode.getIP() + ":" + selfNode.getPort();
	}

	public void getCurrentState() {
		printTable();
	}

	// used in pushService
	public void shareNodes() {
		// select node one node in table
		NodeEntry selected = randomNode();
		// no entries to share
		if (selected == null) {
			return;
		}
		// establish connection to this node
		Socket sharingSocket = connecting(selected.getPort(), selected.getIP());
		// create streams
		try {
			DataOutputStream out = new DataOutputStream(sharingSocket.getOutputStream());
			DataInputStream in = new DataInputStream(sharingSocket.getInputStream());
			JSONObject jsonTable = buildJsonObjectOfTable();
			// send all node entries to this node
			out.writeUTF(jsonTable.toString());
			JSONObject answer = new JSONObject(in.readUTF());
			JSONArray tableArray = (JSONArray) answer.get("table");
			for (int i = 0; i < tableArray.length(); i++) {
				JSONObject tmp = (JSONObject) tableArray.get(i);
				NodeEntry tmp2 = new NodeEntry(tmp.getString("IP"), tmp.getInt("port"), tmp.getString("name"));
				addEntry(tmp2);
			}
			deleteInvalid();
			System.out.print(".");
		} catch (IOException e) {
			if (!e.getMessage().equals("Socket is closed"))
				e.printStackTrace();
		} catch (JSONException e) {
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
	 * @param info
	 *            - text to send json object {nodes: table, message: text to
	 *            send}
	 */
	public void oneToAll(String info) {
		JSONObject toAll = new JSONObject();
		ArrayList<NodeEntry> informedNodes = new ArrayList<NodeEntry>();
		informedNodes.add(getSelfNode());
		JSONArray informed = new JSONArray(informedNodes);
		try {
			toAll.put("all", info);
			toAll.put("informed", informed);
			toAll.put("hops", 0);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		System.out.println(toAll);

		// send to one random node
		NodeEntry next = randomNode();
		Socket infoSocket = connecting(next.getPort(), next.getIP());
		try {
			DataOutputStream out = new DataOutputStream(infoSocket.getOutputStream());
			out.writeUTF(toAll.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			infoSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// parse table of nodes that know the info
	// send info to nodes that don't know the info and add this nodes
	// five times no new nodes to the table of known nodes -> finish
	// TODO catch exception if node already exited -> select new
	public void forwardOneToAll(JSONObject message) {
		// get already informed nodes
		ArrayList<NodeEntry> informedNodes = new ArrayList<NodeEntry>();
		try {
			JSONArray informed = message.getJSONArray("informed");
			for (int i = 0; i < informed.length(); i++) {
				JSONObject tmp = (JSONObject) informed.get(i);
				NodeEntry tmp2 = new NodeEntry(tmp.getString("IP"), tmp.getInt("port"), tmp.getString("name"));
				informedNodes.add(tmp2);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		// get uninformed nodes from own table
		ArrayList<NodeEntry> uninformed = new ArrayList<>();
		for (int i = 0; i < table.size(); i++) {
			if (!informedNodes.contains(table.get(i))) {
				uninformed.add(table.get(i));
			}
		}

		// check if self already get the message
		int prevHops = 0;
		try {
			prevHops = message.getInt("hops");
		} catch (JSONException e2) {
			e2.printStackTrace();
		}
		int wall = Math.max(table.size()*2, informedNodes.size()*2);
		if (informedNodes.contains(getSelfNode()) && prevHops < wall) { // only forwarding
			// increment hops
			try {
				int hops = message.getInt("hops");
				hops++;
				message.put("hops", hops);
			} catch (JSONException e) {
				e.printStackTrace();
			}
					
			// send to one random node in table
			NodeEntry next = randomNode();
			Socket forwardSocket = connecting(next.getPort(), next.getIP());
			//System.out.println("only forwarding");
			try {
				DataOutputStream out = new DataOutputStream(forwardSocket.getOutputStream());
				out.writeUTF(message.toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			
		} else if (prevHops < wall){
			try {
				System.out.println(message.get("all"));
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			
			// build json of informedNodes
			informedNodes.add(getSelfNode());
			JSONArray informed = new JSONArray(informedNodes);
			try {
				message.put("informed", informed);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			NodeEntry next = null;
			if (uninformed.size() > 0) { // select one random entry
				next = randomNode(uninformed);
			} else { // forward to one already informed node and increment hop
				next = randomNode();
				int hops;
				try {
					hops = message.getInt("hops");
					message.put("hops", hops++);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
			}

			// forward message
			Socket forwardSocket = connecting(next.getPort(), next.getIP());
			try {
				DataOutputStream out = new DataOutputStream(forwardSocket.getOutputStream());
				out.writeUTF(message.toString());
				//System.out.println("forwarded");
			} catch (IOException e) {
				if (e.getMessage().equals("Socket is closed")) {
					removeEntry(next.getIP(), next.getPort());
					uninformed.remove(next);
				} else
					e.printStackTrace();
			}
			try {
				forwardSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * 
	 * @param ipPort
	 *            : format will be <ip address>:<port>
	 */
	public void addEntry(NodeEntry ipPort) {
		table.add(ipPort);
	}

	public void deleteInvalid() {
		// delete invalid nodes
		deleteSelfRef();
		deleteDoubleRef();
		// limit of node entries in table //TODO eventuell ändern
		int maxNumberOfNodes = 5;
		while (table.size() > maxNumberOfNodes) {
			int rand = new Random().nextInt(table.size());
			table.remove(rand);
		}
	}

	// remove entry from table (node is offline)
	public void removeEntry(String ip, int port) {
		Iterator<NodeEntry> iter = table.iterator();
		while (iter.hasNext()) {
			NodeEntry tmp = iter.next();
			if (tmp.getIP().equals(ip) && tmp.getPort() == port)
				iter.remove();
		}
	}

	public void setClient(Socket client) {
		this.client = client;
	}

	public void setListener(ServerSocket server) {
		listener = server;
	}

	public Socket getClient() {
		return client;
	}

	public ServerSocket getListener() {
		return listener;
	}

	public void setPushServiceThread(Thread push) {
		pushService = push;
	}

	public NodeEntry getSelfNode() {
		return selfNode;
	}

	public void printTable() {
		for (int i = 0; i < table.size(); i++) {
			System.out.println((i + 1) + ")" + table.get(i).getIP() + ":" + table.get(i).getPort() + "\t" + table.get(i).getName());
		}
		if (table.size() == 0) {
			System.out.println("no entries available");
		}
	}

	public void printTable(ArrayList<NodeEntry> table) {
		for (int i = 0; i < table.size(); i++) {
			System.out.println((i + 1) + ")" + table.get(i).getIP() + ":" + table.get(i).getPort() + "\t" + table.get(i).getName());
		}
		if (table.size() == 0) {
			System.out.println("no entries available");
		}
	}

	public boolean isRunning() {
		return !listener.isClosed();
	}

	private NodeEntry randomNode() {
		int range = table.size();
		if (range == 0)
			return null;
		else {
			int randIndex = new Random().nextInt(range);
			return table.get(randIndex);
		}
	}

	private NodeEntry randomNode(ArrayList<NodeEntry> table) {
		int range = table.size();
		if (range == 0)
			return null;
		else {
			int randIndex = new Random().nextInt(range);
			return table.get(randIndex);
		}
	}

	public JSONObject buildJsonObjectOfTable() {
		JSONObject jsonTable = new JSONObject();
		@SuppressWarnings("unchecked")
		ArrayList<NodeEntry> tmp = (ArrayList<NodeEntry>) table.clone();
		tmp.add(getSelfNode());
		JSONArray node = new JSONArray(tmp);
		try {
			jsonTable.put("table", node);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonTable;
	}

	public JSONObject buildJsonObjectOfTable(ArrayList<NodeEntry> table) {
		JSONObject jsonTable = new JSONObject();
		@SuppressWarnings("unchecked")
		ArrayList<NodeEntry> tmp = (ArrayList<NodeEntry>) table.clone();
		tmp.add(getSelfNode());
		JSONArray node = new JSONArray(tmp);
		try {
			jsonTable.put("table", node);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonTable;
	}

	// delete self reference
	private void deleteSelfRef() {
		Iterator<NodeEntry> iter = table.iterator();
		while (iter.hasNext()) {
			NodeEntry tmp = iter.next();
			if (tmp.getIP().equals(selfNode.getIP()) && tmp.getPort() == selfNode.getPort()) {
				iter.remove();
			}
		}
	}

	// delete duplicated entries
	private void deleteDoubleRef() {
		for (int i = 0; i < table.size(); i++) {
			for (int j = i + 1; j < table.size(); j++) {
				if (table.get(i).getIP().equals(table.get(j).getIP()) && table.get(i).getPort() == table.get(j).getPort()) {
					table.remove(j);
				}
			}
		}
	}

	public ArrayList<NodeEntry> getTable() {
		return table;
	}

}
