package chord;

public interface ChordIF {
	public void join(Chord c);
	public void sendMSG(String text, int target, boolean flag, int msgIndex, boolean first);

}
