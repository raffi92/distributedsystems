package test;

import protocol.Protocol;
import chord.Chord;

public class Test1ab {
	public static void main(String[] args) {
		Protocol protocol = Protocol.getInstance();
		Chord c1 = new Chord(1);
		new Chord(3, 1);
		new Chord(7, 1);
		new Chord(8, 1);
		new Chord(13, 1);
		new Chord(15, 1);
		new Chord(18, 1);
		Chord c25 = new Chord(25, 1);
		new Chord(27, 1);
		
		protocol.printTables();
		protocol.initHops(2);
		c1.sendMSG("test", 27, 0);
		c25.sendMSG("test", 8, 1);
	}

}
