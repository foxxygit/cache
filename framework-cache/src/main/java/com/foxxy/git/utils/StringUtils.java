package com.foxxy.git.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Base64;

public final class StringUtils {

    private static final String UTF_8 = "UTF-8";

    /**
     * 把逗号隔开的字符串型的ID转换成LIST
     * 
     * @param idsStr
     */
    public static List<Integer> toList(String idsStr, String spilt) {
        List<Integer> idList = new ArrayList<Integer>();
        if (org.apache.commons.lang3.StringUtils.isBlank(idsStr)) {
            return idList;
        }
        String[] ids = idsStr.split(spilt);
        for (String id : ids) {
            idList.add(Integer.parseInt(id));
        }
        return idList;
    }

    public static String replaceItemContent(String str) {
        String content = "";
        try {
            content = URLEncoder.encode(str, UTF_8);
            content = content.replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return content;
    }

    public static String decodeContent(String content) {
        if (org.apache.commons.lang3.StringUtils.isBlank(content)) {
            return content;
        }
        try {
            content = URLDecoder.decode(content, UTF_8);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return content;
    }

    public static Integer defaultValue(String value, Integer defaultValue) {
        if (org.apache.commons.lang3.StringUtils.isBlank(value)) {
            return defaultValue;
        }
        try {
            return Integer.valueOf(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static String defaultValue(String value, String defaultValue) {
        if (org.apache.commons.lang3.StringUtils.isBlank(value)) {
            return defaultValue;
        }
        return value;
    }

    public static String decodeBase64(String content) {
        if (org.apache.commons.lang3.StringUtils.isBlank(content)) {
            return content;
        }
        try {
            return new String(Base64.decodeBase64(content), UTF_8);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

}
