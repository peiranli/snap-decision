package com.example.dots;

/**
 * The encryptor used for encoding the usernames of the
 * users using the app. This is used for both the datafiles and the
 * files uploaded to parse.
 */
public class Encryptor {

	public static String encrypt(String plainText, String android_id) {
		String secretKey = "SECRETKEY";
		StringBuffer encryptedString = new StringBuffer();
		int encryptedInt;
		StringBuffer text = new StringBuffer();
		text.append(plainText);

		for (int i = 0; i < text.length(); i++) {
			int plainTextInt = (int) (text.charAt(i) - 'A');
			int secretKeyInt = (int)
					(secretKey.charAt(i % secretKey.length())- 'A');
			encryptedInt = (plainTextInt + secretKeyInt) % 26;
			encryptedString.append((char) ((encryptedInt) + (int) 'A'));
		}
		encryptedString.append(android_id);
		return encryptedString.toString();
	}

	public static String decrypt(String decryptedText) {
		String secretKey = "SECRETKEY";
		StringBuffer decryptedString = new StringBuffer();
		int decryptedInt;
		for (int i = 0; i < decryptedText.length(); i++) {
			int decryptedTextInt = (int) (decryptedText.charAt(i) - 'A');
			int secretKeyInt = (int) (secretKey.charAt(i) - 'A');
			decryptedInt = decryptedTextInt - secretKeyInt;
			if (decryptedInt < 1)
				decryptedInt += 26;
			decryptedString.append((char) ((decryptedInt) + (int) 'A'));
		}
		return decryptedString.toString();
	}
}