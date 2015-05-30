package encryption;

public abstract class EncryptionIF{
	public void setKey(String keyString) {
	}
	public int[] encrypt(final String plaintext) {
		return null;
	}
	public String decrypt(final int[] ciphertext) {
		return null;
	}
	public int[] StringToIntArray(String input) {
		char[] tmp = input.toCharArray();
		int[] output = new int[tmp.length];
		for (int i = 0; i < tmp.length; i++) {
			output[i] = tmp[i];
		}
		return output;
	}
	public String IntArrayToString(int[] input) {
		char[] tmp = new char[input.length];
		for (int i = 0; i < input.length; i++) {
			tmp[i] = (char) input[i];
		}
		return new String(tmp);
	}
}
