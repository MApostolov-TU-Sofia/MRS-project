package com.example.bank_app.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class UtilAdapter {
    // Encode parameter values to be URL-safe
    public static String encodeValue(String value) {
        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return value;
        }
    }

    public static String addSpaces(String text) {
        String result = "";
        for (int i = 0; i < text.length(); i++) {
            result += text.charAt(i);
            if ((i + 1) % 4 == 0) {
                result += " ";
            }
        }
        return result;
    }
}
