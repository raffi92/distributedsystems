package test;

import java.util.ArrayList;
import java.util.Random;

import chord.Chord;
import protocol.Protocol;

public class Test1c {
	private static ArrayList<Integer> ids = new ArrayList<>();
	private static int m = 12;
	private static int numberOfTests = 100;
	public static void main(String[] args) {
		Protocol protocol = Protocol.getInstance();
		protocol.setM(m);
		protocol.setOutputflag(false);
		Random random = new Random();
		for (int j = 0; j < numberOfTests; j++){
			new Chord(1);		// first node
			for (int i = 2; i <=100; i++){
				int newId = random.nextInt((int) Math.pow(2, m)-1);
				while(ids.contains(newId)){
					newId = random.nextInt((int) Math.pow(2, m)-1);
				}
				new Chord(newId, 1);
			}
			//protocol.printTables();
			
			for (int i = 0; i < protocol.getChords().size(); i++){
				int randTargetIndex = random.nextInt(protocol.getChords().size());
				// disable send message to yourself
				while (randTargetIndex == i){
					randTargetIndex = random.nextInt(protocol.getChords().size());
				}
				Chord source = protocol.getChords().get(i);
				int targetIndex = protocol.getChords().get(randTargetIndex).getId();
				source.sendMSG("test", targetIndex, false, i, true);
			}
			
			//protocol.printHops();
			protocol.calcMinMaxAvgHops();
		}
		
	}
	
	

}
