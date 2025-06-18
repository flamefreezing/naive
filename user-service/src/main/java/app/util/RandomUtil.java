package app.util;

import java.math.BigInteger;
import java.security.SecureRandom;

public class RandomUtil {
    public static String generateRandom() {
        return new BigInteger(130, new SecureRandom()).toString(32);
    }
}
