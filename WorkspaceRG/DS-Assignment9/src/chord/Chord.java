package chord;

import java.util.ArrayList;

import protocol.Protocol;

public class Chord implements ChordIF{
	int id; 
	int predecessor;
	private ArrayList<TableEntry> fingertable;
	private Protocol protocol;
	
	public Chord(int id){
		this.id = id;
		protocol = Protocol.getInstance();
		fingertable = new ArrayList<>();
		initFingerTable();
		protocol.addChord(this);
		System.out.println("New network created\nFirst chord has id " + id);
	}
	
	public Chord(int id, int joinId){
		this.id = id;
		fingertable = new ArrayList<>();
		protocol = Protocol.getInstance();
		initFingerTable();
		Chord joinChord = protocol.findId(joinId);
		joinChord.join(this);
		if (protocol.getOutputflag())
			System.out.println("New node with id " + id);
	}

	@Override
	public void join(Chord c) {
		protocol.addChord(c);
	}

	@Override
	public void sendMSG(String text, int target, boolean flag, int msgIndex, boolean first) {
		
		
		if (id != target){
			int nextNodeId = findNextTarget(target, flag);
			if (flag){
				System.out.println("send message from " + id + " to " + nextNodeId);
			}
			protocol.incHops(msgIndex, first);
			Chord nextChord = protocol.findId(nextNodeId);
			nextChord.sendMSG(text, target, flag, msgIndex, false);
		} else {
			if (flag)
				System.out.println("Node " + id + " received message: " + text );
		}
	}
	
	private void initFingerTable(){
		int m = protocol.getM();
		for (int i = 1; i <= m; i++){
			int start = (id + (int) Math.pow(2, (i-1))) % (int) Math.pow(2, protocol.getM()); // n + 2^(i-1) % 2^m
			int node = protocol.findNextNode(id, start);
			fingertable.add(new TableEntry(i, start, node));
		}
		predecessor = protocol.lookupPredecessor(id);
	}
	
	public int getId(){
		return id;
	}
	
	public void printFinger(){
		System.out.println("index \t start \t node");
		for (int i = 0; i<fingertable.size(); i++){
			TableEntry finger = fingertable.get(i);
			System.out.println(finger.getIndex() + "\t" + finger.getStart() + "\t" + finger.getNode());
		}
	}
	
	public TableEntry getFinger(int index){
		return fingertable.get(index);
	}
	
	public void setFinger(int index, int node){
		fingertable.get(index).setNode(node);
	}
	
	public int getPredecessor(){
		return predecessor;
	}
	
	public void setPredecessor(int id){
		predecessor = id;
	}
	// distance from node where finger starts and the real node
	public int getDistance(int index){
		TableEntry t = fingertable.get(index);
		int d = 0;
		if (t.getNode() - t.getStart() < 0){
			d = (int) Math.pow(2, protocol.getM()) - t.getStart() + t.getNode();
		} 
		else {
			d = t.getNode() - t.getStart();
		}
		return d;
	}
	
	// distance from node where finger starts and the new added node with id
	public int getDistance(int index,  int id){
		TableEntry t = fingertable.get(index);
		int d = 0;
		if (id - t.getStart() < 0){
			d = (int) Math.pow(2, protocol.getM()) - t.getStart() + id;
		} 
		else {
			d = id - t.getStart();
		}
		return d;
	}
	
	private int findNextTarget(int target, boolean flag){
		int nextNodeId = 0;
		int i = fingertable.size()-1;
		for (; i >= 0; i--){
			nextNodeId = fingertable.get(i).getNode();
			if (nextNodeId > id && nextNodeId <= target){
				if (flag)
					System.out.print("Next node index "  + fingertable.get(i).getIndex() + ". ");
				return nextNodeId;
			}
			// nextNodeId goes over the end of the ring
			if (nextNodeId <= target && nextNodeId < id && id > target){
				if (flag)
					System.out.print("Next node index "  + fingertable.get(i).getIndex() + ". ");
				return nextNodeId;
			}
			// nextNodeId is before the end of the ring and target is after the end of the ring
			if (nextNodeId > id && nextNodeId > target && id > target){
				if (flag)
					System.out.print("Next node index "  + fingertable.get(i).getIndex() + ". ");
				return nextNodeId;
			}
				
				
		}
		// TODO bug: because succuessor of node (id) is after target
		// TODO solve: print fingertables and check nodes at index 0
		System.out.println(id + "--" + nextNodeId + "--" + target + "--- " + i + "---" + fingertable.get(0).getNode());
		return target;
	}
	
	public void put(int key, String value){
		getFinger(key).setValue(value);
	}
	
	public String get(int key){
		return fingertable.get(key).getValue().toString();
	}
	
	public boolean contains(String value){
		for(int i = 0; i < fingertable.size(); i++){
			if(getFinger(i).getValue().toString() == value){
				return true;
			}
		}
		return false;
	}
	
	public void remove(int key){
		getFinger(key).setValue(null);
	}
}
