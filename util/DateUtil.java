package util;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

public class DateUtil {
    private DateUtil() { }

    /** Accepts yyyy-MM-dd. Returns null when the text is not a valid date. */
    public static LocalDate parseDate(String text) {
        try {
            return LocalDate.parse(text.trim());
        } catch (DateTimeParseException | NullPointerException e) {
            return null;
        }
    }

    /** Accepts HH:mm (24 hour). Returns null when the text is not a valid time. */
    public static LocalTime parseTime(String text) {
        try {
            return LocalTime.parse(text.trim());
        } catch (DateTimeParseException | NullPointerException e) {
            return null;
        }
    }

    /** Days from today until the given date (negative if it is in the past). */
    public static long daysLeft(LocalDate date) {
        return ChronoUnit.DAYS.between(LocalDate.now(), date);
    }
}
