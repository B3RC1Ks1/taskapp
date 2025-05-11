// DateUtils.java
package com.example.taskapp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
    private static final SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
    public static String formatDate(Long timestamp) {
        if (timestamp == null) {
            return null;
        }
        return sdf.format(new Date(timestamp));
    }
}