package com.kdpark.sickdan.util;

import java.time.LocalDateTime;

public class DateUtil {
    private DateUtil() {}

    public static String dateToString(LocalDateTime dateTime) {
        return new StringBuilder().append(dateTime.getYear())
                .append(dateTime.getMonth())
                .append(dateTime.getDayOfMonth())
                .toString();
    }
}
