package edu.vassar.cmpu203.ecoscoop.src.model;

import android.util.Log;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * A class to represent an authentication key.
 */
public class AuthKey implements Serializable {

    private static final String SALT = "salt";
    private static final String KEY = "key";
    private String salt;
    private String key;

    /**
     * Creates an empty authentication key.
     * Useful for fromMap() serialization.
     */
    private AuthKey() {}

    /**
     * Creates an authentication key with the provided password and a random salt.
     *
     * @param password the password to create the key with.
     */
    public AuthKey(final String password) {
        this(AuthKey.generateSalt(), password);
    }

    /**
     * Creates an authentication key with the provided password and salt.
     *
     * @param salt the salt to create the key with.
     * @param password the password to create the key with.
     */
    private AuthKey(final String salt, final String password) {
        this.salt = salt;
        this.key = AuthKey.generateKey(salt, password);
    }

    /**
     * Checks whether the argument password matches against the key.
     *
     * @param password the password to validate against the key.
     * @return true if the password yields a matching key, false otherwise.
     */
    public boolean validatePassword(final String password) {
        final AuthKey other = new AuthKey(this.salt, password);
        return other.key.equals(this.key);
    }

    private static final int SALT_LEN = 20;
    private static final int KEY_LEN = 40;
    private static final int NITERS = 64000;

    /**
     * Generates a random salt string.
     *
     * @return a random salt string.
     */
    @NonNull
    private static String generateSalt() {
        byte[] salt = new byte[SALT_LEN];
        try {
            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
            sr.nextBytes(salt);
        } catch (NoSuchAlgorithmException e) {
            Log.e("EcoScoop", "Error generating authentication salt", e);
        }
        return Base64.getEncoder().encodeToString(salt);
    }

    /**
     * Generates a cryptographic key from a salt and plaintext password.
     *
     * @param salt the salt to add to the password.
     * @param password the plaintext password.
     * @return the resulting cryptographic key.
     */
    private static String generateKey(String salt, String password) {
        String hashStr = null;
        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] saltBytes = Base64.getDecoder().decode(salt);
            char[] chars = password.toCharArray();
            PBEKeySpec spec = new PBEKeySpec(chars, saltBytes, NITERS, KEY_LEN * Byte.SIZE);
            byte[] hashBytes = skf.generateSecret(spec).getEncoded();
            hashStr = Base64.getEncoder().encodeToString(hashBytes);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            Log.e("EcoScoop", "Error generating authentication key", e);
        }
        return hashStr;
    }

    /**
     * Converts this AuthKey into a key-value map.
     *
     * @return a map representation of this AuthKey.
     */
    @NonNull
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put(SALT, this.salt);
        map.put(KEY, this.key);
        return map;
    }

    /**
     * Builds and returns an AuthKey from a key-value map.
     *
     * @param map the map to build from.
     * @return an AuthKey containing the same information as the map.
     */
    @NonNull
    public static AuthKey fromMap(Map<String, Object> map) {
        AuthKey authKey = new AuthKey();
        authKey.salt = (String) map.get(SALT);
        authKey.key = (String) map.get(KEY);
        return authKey;
    }
}
