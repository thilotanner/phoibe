package util.hashing;

import play.Logger;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashingUtils {

    public static String toHexString(byte bytes[])
    {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(Integer.toHexString(0x0100 + (b & 0x00FF)).substring(1));
        }
        return result.toString();
    }
    
    public static String calculateSHA(String string) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA");
            messageDigest.update(string.getBytes());
            return HashingUtils.toHexString(messageDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            Logger.error("Unable to validate saltedPassword");
            return null;
        }
    }
}
