package com.example.ufsqkbank;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;

public class PasswordHasher {

    private static final int SALT_SIZE = 16;
    private static final int HASH_SIZE = 32;
    private static final int ITERATIONS = 1000;

    public static String hashPassword(String password) {
        if (password == null) {
            throw new IllegalArgumentException("password cannot be null");
        }

        byte[] salt = new byte[SALT_SIZE];
        new SecureRandom().nextBytes(salt);

        byte[] hash = pbkdf2(password, salt, ITERATIONS, HASH_SIZE);

        byte[] combined = new byte[1 + SALT_SIZE + HASH_SIZE];
        combined[0] = 0;
        System.arraycopy(salt, 0, combined, 1, SALT_SIZE);
        System.arraycopy(hash, 0, combined, 1 + SALT_SIZE, HASH_SIZE);

        return Base64.getEncoder().encodeToString(combined);
    }

    public static boolean verifyPassword(String hashedPassword, String password) {
        if (hashedPassword == null) {
            return false;
        }
        if (password == null) {
            throw new IllegalArgumentException("password cannot be null");
        }

        byte[] src = Base64.getDecoder().decode(hashedPassword);
        if (src.length != 1 + SALT_SIZE + HASH_SIZE || src[0] != 0) {
            return false;
        }

        byte[] salt = new byte[SALT_SIZE];
        System.arraycopy(src, 1, salt, 0, SALT_SIZE);

        byte[] storedHash = new byte[HASH_SIZE];
        System.arraycopy(src, 1 + SALT_SIZE, storedHash, 0, HASH_SIZE);


        byte[] passwordHash = pbkdf2(password, salt, ITERATIONS, HASH_SIZE);

        return Arrays.equals(storedHash, passwordHash);
    }


    private static byte[] pbkdf2(String password, byte[] salt, int iterations, int keyLength) {
        try {
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, keyLength * 8);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            return factory.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Error while hashing password", e);
        }
    }


}
