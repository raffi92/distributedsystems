package chord;

import java.io.IOException;
import java.rmi.MarshalledObject;

public class TableEntry {
	private int index;
	private int start;
	private int node;
	private int key;
	private MarshalledObject value;
	
	/**
	 * Exercise01
	 * @param index
	 * @param start
	 * @param node
	 */
	public TableEntry(int index, int start, int node){
		this.index = index;
		this.key = index;
		this.start = start;
		this.node = node;
	}
	
	public void setIndex(int index){
		this.index = index;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getNode() {
		return node;
	}

	public void setNode(int node) {
		this.node = node;
	}

	public int getIndex() {
		return index;
	}
	
	public void setKey(int key){
		this.key = key;
	}
	
	public int getKey(){
		return key;
	}
	
	public void setValue(Object value){
		try {
			if (this.value.equals(null))
				this.value = new MarshalledObject(value);
			else
				this.value = (MarshalledObject) value;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public MarshalledObject getValue(){
		return value;
	}

}
