package com.example.pcstore.utils;

public class StringUtils {

    private StringUtils() {
    }

    public static final String EMPTY = "";

    public static boolean isNullOrEmpty(String val) {
        return val == null || val.trim().isBlank() || val.trim().isEmpty();
    }

    public static boolean isNotNullOrEmpty(String val) {
        return !isNullOrEmpty(val);
    }

    public static boolean equals(CharSequence cs1, CharSequence cs2) {
        return org.apache.commons.lang3.StringUtils.equals(cs1, cs2);
    }

    public static boolean notEquals(CharSequence cs1, CharSequence cs2) {
        return !org.apache.commons.lang3.StringUtils.equals(cs1, cs2);
    }

    public static boolean equalsIgnoreCase(CharSequence cs1, CharSequence cs2) {
        return org.apache.commons.lang3.StringUtils.equalsIgnoreCase(cs1, cs2);
    }

}

