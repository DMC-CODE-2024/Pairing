package com.eirs.pairs.utils;

import org.apache.commons.lang3.StringUtils;

public class StringUtil {

    public static String masking(String string) {
        return String.valueOf(maskAlternateChar(string.toCharArray()));
    }

    private static char[] maskAlternateChar(char[] chars) {
        for (int i = 0; i < chars.length; i++) {
            if (i % 2 == 0)
                chars[i] = '*';
        }
        return chars;
    }
}
