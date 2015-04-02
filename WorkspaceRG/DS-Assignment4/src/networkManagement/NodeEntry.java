package networkManagement;

import java.net.InetAddress;

/*
 * This class describes how a node entry in the remote node table look like
 */
public class NodeEntry {
	private InetAddress ip;
	private int port;
	
	public NodeEntry(InetAddress ip, int port){
		this.ip = ip;
		this.port = port;
	}
	
	public InetAddress getIP(){
		return this.ip;
	}
	
	public int getPort(){
		return this.port;
	}

}
