package encryption;

import java.security.PrivateKey;
import java.security.PublicKey;

public abstract class EncryptionIF {
	public static final String method = "RSA";
	public static final String PRIVATE_KEY_FILE = "private.key";
	public static final String PUBLIC_KEY_FILE = "public.key";
	public static final String pathPrefixServer = "src/server/";
	public static final String pathPrefixClient = "src/client/";

	public void setKey(String keyString) {
	}
	// rc4 int[] based
	public int[] encrypt(final String plaintext) {
		return null;
	}
	// rsa byte [] based
	public byte[] encrypt(String text, PublicKey key) {
		return null;
	}
	// rsa byte [] based
	public String decrypt(byte[] text, PrivateKey key) {
		return null;
	}
	// rc4 int[] based
	public String decrypt(final int[] ciphertext) {
		return null;
	}
	// convert for rc4
	public int[] StringToIntArray(String input) {
		char[] tmp = input.toCharArray();
		int[] output = new int[tmp.length];
		for (int i = 0; i < tmp.length; i++) {
			output[i] = tmp[i];
		}
		return output;
	}
	// convert for rc4
	public String IntArrayToString(int[] input) {
		char[] tmp = new char[input.length];
		for (int i = 0; i < input.length; i++) {
			tmp[i] = (char) input[i];
		}
		return new String(tmp);
	}
}
