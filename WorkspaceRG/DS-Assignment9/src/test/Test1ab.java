package test;

import protocol.Protocol;
import chord.Chord;

public class Test1ab {
	public static void main(String[] args) {
		Protocol protocol = Protocol.getInstance();
		Chord c1 = new Chord(1);
		Chord c3 = new Chord(3, 1);
		Chord c7 = new Chord(7, 1);
		Chord c8 = new Chord(8, 1);
		Chord c13 = new Chord(13, 1);
		Chord c15 = new Chord(15, 1);
		Chord c18 = new Chord(18, 1);
		Chord c25 = new Chord(25, 1);
		Chord c27 = new Chord(27, 1);
		
		protocol.printTables();
		c1.sendMSG("hallo", 27);
		c25.sendMSG("test", 8);
	}

}
