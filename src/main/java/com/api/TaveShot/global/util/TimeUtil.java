package com.api.TaveShot.global.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeUtil {
    public static String formatCreatedDate(final LocalDateTime localDateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy.MM.dd HH:mm");
        return localDateTime.format(formatter);
    }

    public static String formatNewsletter(final LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return "Unknown";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return localDateTime.format(formatter);
    }
}
