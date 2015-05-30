package encryption;

public class SimpleEncryption extends EncryptionIF{
	private int key;
	@Override
	public void setKey(String keyString) {
		key = Integer.parseInt(keyString);
	}

	@Override
	public int[] encrypt(String in) {
		String out = "";
		for (int i = 0; i < in.length(); i++) {
			char ch = in.charAt(i);
			ch = (char) (' ' + ((ch - ' ' + key) % 98));
			out += ch;
		}
		String keyString = Integer.toString(key);
		int length = keyString.length();
		out = out + keyString + '+' + length;
		System.out.println(out);
		return StringToIntArray(out);
	}

	@Override
	public String decrypt(int[] ciphertext) {
		String out = IntArrayToString(ciphertext);
		String in = "";
		key = 98 - key;
		for (int i = 0; i < out.length(); i++) {
			char ch = out.charAt(i);
			int cache = (' ' + ((ch - ' ' + key) % 98)) + 98;
			ch = (char) cache;
			in += ch;
		}
		return in;
	}

}
