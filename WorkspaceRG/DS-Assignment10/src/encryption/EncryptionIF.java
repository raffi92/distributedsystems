package encryption;

public interface EncryptionIF {
	public void setKey(String keyString);
	public int[] encrypt(final String plaintext);
	public String decrypt(final int[] ciphertext);
	abstract int[] StringToIntArray(String input);
	abstract String IntArrayToString(int[] input);
}
