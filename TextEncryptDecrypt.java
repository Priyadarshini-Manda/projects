import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.security.*;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.Scanner;

public class TextEncryptDecrypt {

    private static final int KEY_SIZE = 256;
    private static final int ITERATIONS = 65536;
    private static final int IV_SIZE = 16;

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== Text Encryption/Decryption Tool ===");

        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        System.out.print("Encrypt or Decrypt? (E/D): ");
        String choice = scanner.nextLine().trim().toUpperCase();

        if (choice.equals("E")) {
            System.out.print("Enter text to encrypt: ");
            String plainText = scanner.nextLine();

            String encrypted = encrypt(plainText, password);
            System.out.println("Encrypted text:");
            System.out.println(encrypted);

        } else if (choice.equals("D")) {
            System.out.print("Enter Base64 text to decrypt: ");
            String encryptedText = scanner.nextLine();

            String decrypted = decrypt(encryptedText, password);
            System.out.println("Decrypted text:");
            System.out.println(decrypted);

        } else {
            System.out.println("Invalid choice. Exiting.");
        }

        scanner.close();
    }

    private static SecretKey generateKey(String password, byte[] salt) throws Exception {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_SIZE);
        SecretKey tmp = factory.generateSecret(spec);
        return new SecretKeySpec(tmp.getEncoded(), "AES");
    }

    public static String encrypt(String plainText, String password) throws Exception {
        byte[] salt = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(salt);

        SecretKey key = generateKey(password, salt);

        byte[] iv = new byte[IV_SIZE];
        random.nextBytes(iv);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);

        byte[] encrypted = cipher.doFinal(plainText.getBytes("UTF-8"));

        // Combine salt + iv + encrypted bytes
        byte[] combined = new byte[salt.length + iv.length + encrypted.length];
        System.arraycopy(salt, 0, combined, 0, salt.length);
        System.arraycopy(iv, 0, combined, salt.length, iv.length);
        System.arraycopy(encrypted, 0, combined, salt.length + iv.length, encrypted.length);

        return Base64.getEncoder().encodeToString(combined);
    }

    public static String decrypt(String encryptedText, String password) throws Exception {
        byte[] combined = Base64.getDecoder().decode(encryptedText);

        byte[] salt = new byte[16];
        System.arraycopy(combined, 0, salt, 0, 16);

        byte[] iv = new byte[IV_SIZE];
        System.arraycopy(combined, 16, iv, 0, IV_SIZE);

        byte[] encrypted = new byte[combined.length - 16 - IV_SIZE];
        System.arraycopy(combined, 16 + IV_SIZE, encrypted, 0, encrypted.length);

        SecretKey key = generateKey(password, salt);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);

        byte[] decrypted = cipher.doFinal(encrypted);
        return new String(decrypted, "UTF-8");
    }
}