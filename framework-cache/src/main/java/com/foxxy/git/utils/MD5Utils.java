package com.foxxy.git.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Utils {

    private static final char[] HEX_CHARS = "0123456789abcdef".toCharArray();

    public static String asHex(byte[] buf) {
        char[] chars = new char[2 * buf.length];
        for (int i = 0; i < buf.length; ++i) {
            chars[2 * i] = HEX_CHARS[(buf[i] & 0xF0) >>> 4];
            chars[2 * i + 1] = HEX_CHARS[buf[i] & 0x0F];
        }
        return new String(chars);
    }

    public static String getMD5(String s) {
        try {
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();
            return asHex(messageDigest);
        } catch (NoSuchAlgorithmException e) {
            // donothing
        }
        return org.apache.commons.lang3.StringUtils.EMPTY;
    }
}
