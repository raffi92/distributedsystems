package encryption;

public abstract class EncryptionAbstract implements EncryptionIF{
	@Override
	public int[] StringToIntArray(String input) {
		char[] tmp = input.toCharArray();
		int[] output = new int[tmp.length];
		for (int i = 0; i < tmp.length; i++) {
			output[i] = tmp[i];
		}
		return output;
	}
	@Override
	public String IntArrayToString(int[] input) {
		char[] tmp = new char[input.length];
		for (int i = 0; i < input.length; i++) {
			tmp[i] = (char) input[i];
		}
		return new String(tmp);
	}
}
