package util.hashing;

public class HashingUtils {
    public static String toHexString(byte bytes[])
    {
        StringBuffer result = new StringBuffer();
        for (byte b : bytes) {
            result.append(Integer.toHexString(0x0100 + (b & 0x00FF)).substring(1));
        }
        return result.toString();
    }
}
