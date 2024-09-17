package com.eirs.pairs.utils;

import java.security.SecureRandom;
import java.time.LocalDateTime;

public class RandomIdGeneratorUtil {

    private final static String chars = "123456789";

    private static final Integer otpDigits = 6;

    public static Integer getRandomNumbers() {
        return getRandomNumbers(otpDigits);
    }

    public static Integer getRandomNumbers(int otpDigits) {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < otpDigits; i++) {
            int randomIndex = random.nextInt(chars.length());
            sb.append(chars.charAt(randomIndex));
        }
        return Integer.parseInt(sb.toString());
    }

    public static String generateRequestId() {
        return "P" + LocalDateTime.now().format(DateFormatterConstants.requestIdDateFormat) + getRandomNumbers(3);
    }

    public static void main(String[] args) {
        System.out.println(getRandomNumbers());
    }

}
