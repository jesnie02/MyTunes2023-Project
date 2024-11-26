package BLL.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.Key;
import java.util.Base64;
import java.util.Properties;

public class Encryption {

    private static final String PROP_FILE = "config/config.settings";

    private static final String ALGORITHM = "AES";
    private static byte[] keyVal;

    /**
     * Sets keyval from our configuration fil
     * Important: Has to be 16, 24 or 32 bytes long.
     * Example: 1234567891234567 - 16 bytes.
     *
     */
    private static void setKeyFromConfig() throws IOException {
        Properties configProperties = new Properties();
        configProperties.load(new FileInputStream((PROP_FILE)));

        keyVal = configProperties.getProperty("EncryptionKey").getBytes();
    }

    /**
     * Creates a key using our selected
     * encryption method and private key.
     *
     * @return Key.
     *
     */
    private static Key generateKey() throws Exception {
        setKeyFromConfig();
        Key key = new SecretKeySpec(keyVal, ALGORITHM);
        return key;
    }

    /**
     * Generates a cipher using our algorithm
     * and our generateKey method.
     *
     * @return Cipher.
     * will return in selected mode.
     */
    private static Cipher generateCipher(int mode) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(mode, generateKey());
        System.out.println(cipher);

        return cipher;
    }

    /**
     * Encrypts a string using our cipher
     * and makes it to base64.
     *
     * @return String.
     * returns given string encrypted.
     */
    public static String encryptString(String str) throws Exception {
        Cipher cipher = generateCipher(Cipher.ENCRYPT_MODE);

        byte[] encryptedVal = cipher.doFinal(str.getBytes());
        byte[] encryptedByte = Base64.getEncoder().encode(encryptedVal);

        //System.out.println("Encrypted: " + new String(encryptedByte));
        return new String(encryptedByte);
    }

    /**
     * Decrypts a string using our cipher.
     *
     * @return String.
     * returns given string decrypted.
     */
    public static String decryptString(String encryptedValue) throws Exception {
        Cipher cipher = generateCipher(Cipher.DECRYPT_MODE);

        byte[] decodedBytes = Base64.getDecoder().decode(encryptedValue.getBytes());
        byte[] enctVal = cipher.doFinal(decodedBytes);

        //System.out.println("Decrypted Value :: " + new String(enctVal));
        return new String(enctVal);
    }

    /*
      public static void main(String[] args) throws Exception {

        String crypt = encryptString("hej");

        decryptString(crypt);
    }
*/
}
