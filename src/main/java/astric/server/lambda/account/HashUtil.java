package astric.server.lambda.account;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

import static com.amazonaws.util.BinaryUtils.fromHex;
import static com.amazonaws.util.BinaryUtils.toHex;

public class HashUtil {
    public static String hashPassword(String password) {
        String result = null;
        try {
            int iterations = 1000;
            char[] chars = password.toCharArray();
            byte[] salt = new byte[16];
            SecureRandom.getInstance("SHA1PRNG").nextBytes(salt);

            PBEKeySpec spec = new PBEKeySpec(chars, salt, iterations, 64 * 8);
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] hash = skf.generateSecret(spec).getEncoded();
            result = iterations + ":" + toHex(salt) + ":" + toHex(hash);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static boolean validatePassword(String originalPassword, String hashedPassword) throws NoSuchAlgorithmException, InvalidKeySpecException {
        boolean result = false;
        try {
            String[] parts = hashedPassword.split(":");
            int iterations = Integer.parseInt(parts[0]);
            byte[] salt = fromHex(parts[1]);
            byte[] hash = fromHex(parts[2]);

            PBEKeySpec spec = new PBEKeySpec(originalPassword.toCharArray(), salt, iterations, hash.length * 8);
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] testHash = skf.generateSecret(spec).getEncoded();

            int diff = hash.length ^ testHash.length;
            for(int i = 0; i < hash.length && i < testHash.length; i++)
            {
                diff |= hash[i] ^ testHash[i];
            }
            result = diff == 0;
        } catch (NumberFormatException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return result;
    }
}
