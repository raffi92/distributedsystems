package protocol;

import java.util.ArrayList;

import chord.Chord;

/*
 * Singleton class Protocol with manage the chord network
 */
public class Protocol {
	private static Protocol instance;
	private static ArrayList<Chord> nodes;
	private static ArrayList<Integer> hops;
	// m is the number of bits within the address of a node
	private int m = 5; // default 32 nodes
	private boolean flag = true;
	private Protocol() {
		nodes = new ArrayList<Chord>();
		hops = new ArrayList<>();
	};

	public static Protocol getInstance() {
		if (Protocol.instance == null)
			instance = new Protocol();
		return instance;
	}

	public void addChord(Chord c) {
		// inserting sorted
		if (nodes.size() == 0) { // first
			nodes.add(c);
		} else if (nodes.get(0).getId() > c.getId()) { // front
			nodes.add(0, c);
		} else if (nodes.get(nodes.size() - 1).getId() < c.getId()) { // last
			nodes.add(nodes.size(), c);
		} else { // middle
			int i = 0;
			while (nodes.get(i).getId() < c.getId()) {
				i++;
			}
			nodes.add(i, c);
		}

		updateFingers(c.getId());
		
	}

	public int getM() {
		return m;
	}
	
	public void setM(int m){
		this.m = m;
	}
	
	public void incHops(int index, boolean first){
		if (first)
			hops.add(index, 1);
		else {
			int tmp = hops.get(index);
			hops.set(index, ++tmp);
		}
	}
	
	public ArrayList<Chord> getChords(){
		return nodes;
	}

	public int findNextNode(int id, int start) {
		int i = 0;
		if (nodes.size() == 0)
			return id;
		while (i < nodes.size()) {
			if (nodes.get(i).getId() >= start) {
				break;
			}
			i++;
		}
		// point to first node if no greater id found
		if (nodes.get(nodes.size() - 1).getId() < start)
			return nodes.get(0).getId();

		return nodes.get(i).getId();

	}

	// id of the current added finger
	public void updateFingers(int id) {
		Chord next = findId(findNextNode(id, id+1));
		next.setPredecessor(id);
		for (int i = 0; i < m; i++) {
			int preId = lookupPredecessorOfFinger(id-(int)Math.pow(2, i));  // n-2i-1 -> without -1 because we iterate i from 0 to m
			Chord c = findId(preId);
			while (c.getDistance(i) > c.getDistance(i, id)){
				if (c.getId() == 3)
					System.out.println("set finger " + i + " of node " + c.getId() + " to " + id);
				c.setFinger(i, id);
				c = findId(lookupPredecessor(c.getId()));
			}
		}
	}

	public int lookupPredecessor(int id) {
		int i = 0;
		//System.out.println(nodes.size());
		if (nodes.size() == 0)
			return id;
		while (i < nodes.size()) {
			if (nodes.get(i).getId() >= id) {
				break;
			}
			i++;
		}
		// predecessor of node 1 is last node in list
		if (i == 0){
			i = nodes.size();
		}
		return nodes.get(i-1).getId();
	}
	
	public int lookupPredecessorOfFinger(int fingerId){
		int i = 0;
		//System.out.println(nodes.size());
		if (nodes.size() == 0)
			return fingerId;
		while (i < nodes.size()) {
			if (nodes.get(i).getId() == fingerId){
				return nodes.get(i).getId();
			}
			if (nodes.get(i).getId() >= fingerId) {
				break;
			}
			i++;
		}
		// predecessor of node 1 is last node in list
		if (i == 0){
			i = nodes.size();
		}
		return nodes.get(i-1).getId();
	}

	public void printChords() {
		System.out.println("Chord ids:");
		for (Chord c : nodes) {
			System.out.println(c.getId());
		}
	}

	public Chord findId(int id) {
		for (Chord c : nodes) {
			if (c.getId() == id)
				return c;
		}
		return null;
	}
	
	public void printTables(){
		System.out.println("########FINGERTABLES########");
		for (Chord c : nodes){
			System.out.println("Table of node " + c.getId());
			c.printFinger();
			System.out.println("Predecessor: " + c.getPredecessor());
		}
	}
	
	public void printHops(){
		for(int i = 0; i < hops.size(); i++){
			System.out.println("MessageId " + i + ": " + hops.get(i) + " hops");
		}
	}
	
	public void calcMinMaxAvgHops(){
		int min = (int) Math.pow(2, 31); // high int
		int max = -1;
		int sum = 0;
		for (Integer i : hops){
			if (i < min){
				min = i;
			}
			if (i > max){
				max = i;
			}
			sum += i;
		}
		System.out.println("Min: " + min);
		System.out.println("Max: " + max);
		System.out.println("Avg: " + (double)sum/ (double) hops.size());
	}
	
	public void clearLists(){
		hops.clear();
		nodes.clear();
	}
	
	public void setOutputflag(boolean flag){
		this.flag = flag;
	}
	
	public boolean getOutputflag(){
		return flag;
	}
	
}
