package com.arrivnow.usermanagement.usermanagement.util;

import java.security.SecureRandom;
import java.util.Random;

import org.apache.commons.lang3.RandomStringUtils;

/**
 * Utility class for generating random Strings.
 */
public final class RandomUtil {

    private static final int DEF_COUNT = 20;

    private static final SecureRandom SECURE_RANDOM;

    static {
        SECURE_RANDOM = new SecureRandom();
        SECURE_RANDOM.nextBytes(new byte[64]);
    }

    private RandomUtil() {
    }

    /**
     * <p>generateRandomAlphanumericString.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public static String generateRandomAlphanumericString() {
        return RandomStringUtils.random(DEF_COUNT, 0, 0, true, true, null, SECURE_RANDOM);
    }

    /**
     * Generate a password.
     *
     * @return the generated password.
     */
    public static String generatePassword() {
        return generateRandomAlphanumericString();
    }

    /**
     * Generate an activation key.
     *
     * @return the generated activation key.
     */
    public static String generateActivationKey() {
        return generateRandomAlphanumericString();
    }

    /**
     * Generate a reset key.
     *
     * @return the generated reset key.
     */
    public static String generateResetKey() {
        return generateRandomAlphanumericString();
    }
    
    public static char[] generateOTP() {
    	
    	String numbers = "0123456789";
    	Random rndm_method = new Random();
    	char[] otp = new char[6];
    	for (int i = 0; i < 6; i++)
        {
             otp[i] =
             numbers.charAt(rndm_method.nextInt(numbers.length()));
        }
    	
    	 return otp;
    	
    }
}