package encryption;

import java.util.Arrays;

public class RC4 extends EncryptionIF{
	private final int[] S = new int[256];
	private int keylen;

	@Override
	public void setKey(String keyString) {
		int[] key = StringToIntArray(keyString);
		if (key.length < 1 || key.length > 256) {
			throw new IllegalArgumentException("key must be between 1 and 256 bytes");
		} else {
			keylen = key.length;
			for (int i = 0; i < 256; i++) {
				S[i] = i;
			}
			int j = 0;
			int tmp;
			for (int i = 0; i < 256; i++) {
				j = (j + S[i] + key[i % keylen]) % 256;
				tmp = S[j];
				S[j] = S[i];
				S[i] = tmp;
			}
		}
	}

	@Override
	public int[] encrypt(final String plaintext) {
		final int[] plain = StringToIntArray(plaintext);
		final int[] ciphertext = new int[plain.length];
		int i = 0, j = 0, k, t;
		int tmp;
		for (int counter = 0; counter < plain.length; counter++) {
			i = (i + 1) % 256;
			j = (j + S[i]) % 256;
			tmp = S[j];
			S[j] = S[i];
			S[i] = tmp;
			t = (S[i] + S[j]) % 256;
			k = S[t];
			ciphertext[counter] = (int) (plain[counter]) ^ k;
		}
		return ciphertext;
	}

	@Override
	public String decrypt(final int[] ciphertext) {
		return IntArrayToString(encrypt(IntArrayToString(ciphertext)));
	}
	// test rc4
	public static void main(String[] args) {
		String input = "hallo";
		String key = "key";

		RC4 rc4 = new RC4();
		// init key for the sending party
		rc4.setKey(key);
		System.out.println("key: " + new String(key));
		rc4.setKey(key);
		int[] enc = rc4.encrypt(input);
		// init key for the receiving party
		rc4.setKey(key);
		String dec = rc4.decrypt(enc);
		System.out.println("enc: " + Arrays.toString(enc));
		System.out.println("dec: " + dec);
	}
}