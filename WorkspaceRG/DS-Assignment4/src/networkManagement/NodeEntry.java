package networkManagement;


/*
 * This class describes how a node entry in the remote node table look like
 */
public class NodeEntry{
	private String ip;
	private int port;
	private String name;

	// ip address : port (example: 127.0.0.1:8000)
	public NodeEntry(String ipPort, String name) {
		this.ip = ipPort.split(":")[0];
		this.port = Integer.parseInt(ipPort.split(":")[1]);
		this.name = name;
	}
	
	public NodeEntry(String ip, int port, String name){
		this.ip = ip;
		this.port = port;
		this.name = name;
	}

	public String getIP() {
		return this.ip;
	}

	public int getPort() {
		return this.port;
	}
	
	public String getName(){
		return this.name;
	}
	
	@Override
	public boolean equals(Object v) {
		boolean retVal = false;
        if (v instanceof NodeEntry){
        	NodeEntry ptr = (NodeEntry) v;
        	retVal = ptr.getIP().equals(this.getIP()) && ptr.getPort() == this.getPort();
        }
        return retVal;
	}
}
