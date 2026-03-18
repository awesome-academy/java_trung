package com.example.foodsdrinks.config;

import java.time.format.DateTimeFormatter;

public final class DateTimeFormatterConstants {

    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private DateTimeFormatterConstants() {}
}
