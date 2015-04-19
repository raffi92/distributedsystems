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
	ArrayList<NodeEntry> removedNodes = new ArrayList<>();
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
				removedNodes.add(new NodeEntry(ip, port, null));
				return null;
			} else {
				e.printStackTrace();
			}
		}
		return client;
	}
	
	public Socket initConnecting(int port, String ip) {
		try {
			client = new Socket(ip, port);
		} catch (IOException e) {
			System.out.println("connection unsuccessfully");
			return null;
		}
		return client;
	}
	// disconnet socket
	public void disconnecting(Socket socket){
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	// quit peer
	public void quit() {
		try {
			listener.close();
			pushService.interrupt();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setLocalPeerAddress(ServerSocket sock, String name) {
		// get public ip
//		BufferedReader in = null;
//        try {
//        	URL whatismyip = new URL("http://checkip.amazonaws.com");
//            
//            in = new BufferedReader(new InputStreamReader(
//                    whatismyip.openStream()));
//            String ip = in.readLine();
//            System.out.println(ip);
//        } catch (MalformedURLException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		} finally {
//            if (in != null) {
//                try {
//                    in.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
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
		// delete removed nodes
		removedNodes.clear();
		// select node one node in table
		NodeEntry selected = randomNode();
		// no entries to share
		if (selected == null) {
			return;
		}
		// establish connection to this node
		Socket sharingSocket = connecting(selected.getPort(), selected.getIP());
		while (sharingSocket == null){
			selected = randomNode();
			if (selected == null) {
				return;
			}
			sharingSocket = connecting(selected.getPort(), selected.getIP());
		}
		// create streams
		try {
			DataOutputStream out = new DataOutputStream(sharingSocket.getOutputStream());
			DataInputStream in = new DataInputStream(sharingSocket.getInputStream());
			JSONObject jsonTable = buildJsonObjectOfTable();
			// add removed nodes
			if (removedNodes.size() >  0){
				JSONArray invalidNodes = new JSONArray(removedNodes);
				jsonTable.put("removed", invalidNodes);
			}
			// send all node entries to this node
			out.writeUTF(jsonTable.toString());
			JSONObject answer = new JSONObject(in.readUTF());
			JSONArray tableArray = (JSONArray) answer.get("table");
			parseJsonArray(tableArray);
			deleteInvalid();
			System.out.print(".");
		} catch (IOException e) {
			if (!e.getMessage().equals("Socket is closed"))
				e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		// close
		disconnecting(sharingSocket);
	}

	/**
	 * 
	 * @param info
	 *            - text to send json object {informed nodes: table, message: text to
	 *            send, hops: number of double informed nodes}
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

		// send to one random node
		NodeEntry next = randomNode();
		Socket infoSocket = connecting(next.getPort(), next.getIP());
		while (infoSocket == null){
			next = randomNode();
			if (next == null) {
				return;
			}
			infoSocket = connecting(next.getPort(), next.getIP());
		}
		// no connection
		if (infoSocket != null){
			try {
				DataOutputStream out = new DataOutputStream(infoSocket.getOutputStream());
				out.writeUTF(toAll.toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
			disconnecting(infoSocket);
		}
	}

	// parse table of nodes that know the info
	// send info to nodes that don't know the info and add this nodes
	// finish if hops is to high
	public void forwardOneToAll(JSONObject message) {
		// get already informed nodes
		ArrayList<NodeEntry> informedNodes = new ArrayList<NodeEntry>();
		try {
			JSONArray informed = message.getJSONArray("informed");
			informedNodes = parseJsonArray(informed, informedNodes);
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
		// define some maximum number of hops without changes of the informed nodes table
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
			while (forwardSocket == null){
				next = randomNode();
				if (next == null) {
					return;
				}
				forwardSocket = connecting(next.getPort(), next.getIP());
			}
			
			if (forwardSocket != null){
				try {
					DataOutputStream out = new DataOutputStream(forwardSocket.getOutputStream());
					out.writeUTF(message.toString());
				} catch (IOException e) {
					e.printStackTrace();
				}
				disconnecting(forwardSocket);
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
			// select next node
			NodeEntry next = null;
			Socket forwardSocket = null;
			while (forwardSocket == null){
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
				if (next == null)
					return;
				forwardSocket = connecting(next.getPort(), next.getIP());
			}
			// forward message
			if (forwardSocket != null){
				try {
					DataOutputStream out = new DataOutputStream(forwardSocket.getOutputStream());
					out.writeUTF(message.toString());
				} catch (IOException e) {
					if (e.getMessage().equals("Socket is closed")) {
						removeEntry(next.getIP(), next.getPort());
						uninformed.remove(next);
					} else
						e.printStackTrace();
				}
				disconnecting(forwardSocket);
			}
		}
	}
	// initial message to send a one-to-one message
	// format
	// message: text to send
	// target: name of the receiver
	// hops: number of double asked nodes
	// sender: address of the sender
	public void contactPeer(String name, String message){
		// build json object
		JSONObject mes = new JSONObject();
		try {
			mes.put("message", message);
			mes.put("target", name);
			mes.put("hops", 0);
			mes.put("sender", getSelfNode().getIP() + ":" + getSelfNode().getPort());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		// search for target in own table
		NodeEntry next = searchByName(name);
		// select random node
		if (next == null){
			next = randomNode();
		} else {
			System.out.println("peer found in own table");
			sendOneToOne(next.getIP(), next.getPort(), message);
			return;
		}
		// no entries in table
		if (next == null){
			System.out.println("not connected to a peer");
			return;
		}
			
		// connect
		Socket nextSocket = null;
		nextSocket = connecting(next.getPort(), next.getIP());
		// connect
		while (nextSocket == null){
			next = randomNode();
			// no entries in table
			if (next == null)
				return;
			nextSocket = connecting(next.getPort(), next.getIP());
		}
		// send 
		try {
			DataOutputStream out = new DataOutputStream(nextSocket.getOutputStream());
			out.writeUTF(mes.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		// close
		disconnecting(nextSocket);
	}
	// lookup function to get the receiver address
	// add targetIp and targetPort if target is found in some table
	// not found -> add reachable = false to the message
	public void lookUp(JSONObject mes){
		// parse jsonObject
		String target = null;
		String sender = null;
		String text = null;
		int hops = 0;
		try {
			target = mes.getString("target");
			sender = mes.getString("sender");
			text = mes.getString("message");
			hops = mes.getInt("hops");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		// check if target is in current nodes table
		NodeEntry next = searchByName(target);
		boolean targetFound = false;
		// select random node
		if (next == null ){
			// inform sender that receiver is not reachable
			if (hops > Math.max(table.size()*20, 15)){
				next = new NodeEntry(sender.split(":")[0], Integer.parseInt(sender.split(":")[1]), null);
				mes = new JSONObject();
				
				try {
					mes.put("reachable", false);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else {
				next = randomNode();
			}
		} else {
			targetFound = true;
			// add information about target
			mes = new JSONObject();
			try {
				mes.put("targetIP", next.getIP());
				mes.put("targetPort", next.getPort());
				mes.put("message", text);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			// inform sender
			next = new NodeEntry(sender.split(":")[0], Integer.parseInt(sender.split(":")[1]), null);
		}
		// connect and send
		Socket forward = connecting(next.getPort(), next.getIP());
		if (targetFound && forward == null){
			System.out.println("sender exited");
			return;
		}
		if (!targetFound){
			while (forward == null){
				next = randomNode();
				if (next == null)
					return;
				forward = connecting(next.getPort(), next.getIP());
			}
			try {
				mes.put("hops", (++hops));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		// forward message
		if (forward != null){
			try {
				DataOutputStream out = new DataOutputStream(forward.getOutputStream());
				out.writeUTF(mes.toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
			// close
			disconnecting(forward);
		}
		
		
	}
	// send the message if sender got the address of the receiver
	public void sendOneToOne(String ip, int port, String message){
		JSONObject mes = new JSONObject();
		try {
			mes.put("oneToOneMessage", message);
			mes.put("sendFrom", selfNode.getName());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		// connect 
		Socket target = connecting(port, ip);
		if (target == null){
			System.out.println("receiver offline");
			return;
		}
		// send
		try {
			DataOutputStream out = new DataOutputStream(target.getOutputStream());
			out.writeUTF(mes.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		disconnecting(target);
	}

	// add new entry to table. format will be <ip address>:<port>
	public void addEntry(NodeEntry ipPort) {
		table.add(ipPort);
	}
	// delete double and self references and reduce size of table
	public void deleteInvalid() {
		// delete invalid nodes
		deleteSelfRef();
		deleteDoubleRef();
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
	// get self-reference
	public NodeEntry getSelfNode() {
		return selfNode;
	}
	// print own table
	public void printTable() {
		for (int i = 0; i < table.size(); i++) {
			System.out.println((i + 1) + ")" + table.get(i).getIP() + ":" + table.get(i).getPort() + "\t" + table.get(i).getName());
		}
		if (table.size() == 0) {
			System.out.println("no entries available");
		}
	}
	// print given table
	public void printTable(ArrayList<NodeEntry> table) {
		for (int i = 0; i < table.size(); i++) {
			System.out.println((i + 1) + ")" + table.get(i).getIP() + ":" + table.get(i).getPort() + "\t" + table.get(i).getName());
		}
		if (table.size() == 0) {
			System.out.println("no entries available");
		}
	}
	// check activity
	public boolean isRunning() {
		return !listener.isClosed();
	}
	// select random node of own table
	private NodeEntry randomNode() {
		int range = table.size();
		if (range == 0)
			return null;
		else {
			int randIndex = new Random().nextInt(range);
			return table.get(randIndex);
		}
	}
	// select random node of given table
	private NodeEntry randomNode(ArrayList<NodeEntry> table) {
		int range = table.size();
		if (range == 0)
			return null;
		else {
			int randIndex = new Random().nextInt(range);
			return table.get(randIndex);
		}
	}
	// build json object of own node table
	@SuppressWarnings("unchecked")
	public JSONObject buildJsonObjectOfTable() {
		JSONObject jsonTable = new JSONObject();
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
	// build json object of node table given by the parameter
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
	// get own table
	public ArrayList<NodeEntry> getTable() {
		return table;
	}
	// used for table
	public void parseJsonArray(JSONArray input){
		// parse each element
		for (int i = 0; i < input.length(); i++) {
			JSONObject tmp1 = null;
			NodeEntry tmp2 = null;
			try {
				tmp1 = (JSONObject) input.get(i);
				tmp2 = new NodeEntry(tmp1.getString("IP"), tmp1.getInt("port"), tmp1.getString("name"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			addEntry(tmp2);
		}
	}
	// used for informed nodes in one-to-all messages
	public ArrayList<NodeEntry> parseJsonArray(JSONArray input, ArrayList<NodeEntry> output){
		for (int i = 0; i < input.length(); i++) {
			JSONObject tmp;
			NodeEntry tmp2 = null;
			try {
				tmp = (JSONObject) input.get(i);
				tmp2 = new NodeEntry(tmp.getString("IP"), tmp.getInt("port"), tmp.getString("name"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			output.add(tmp2);
		}
		return output;
	}
	// used for removed nodes in the table
	public void removeInvalidNodes(JSONArray input){
		// parse each element
		for (int i = 0; i < input.length(); i++) {
			JSONObject tmp1 = null;
			try {
				tmp1 = (JSONObject) input.get(i);
				removeEntry(tmp1.getString("IP"), tmp1.getInt("port"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	// used in lookup
	private NodeEntry searchByName(String target){
		for (int i = 0; i < table.size(); i++){
			if (table.get(i).getName().equals(target)){
				return table.get(i);
			}
		}
		return null;
	}

}
