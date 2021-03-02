package seedu.address.model;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.util.Locale;

public class Date {
    public static final String MESSAGE_CONSTRAINTS =
        "Dates should be given in the following formats (dd/MM/yyyy or yyyy-MM-dd or MMM d yyyy)";

    public static final DateTimeFormatter INPUT_DATE_TIME_FORMATTER = new DateTimeFormatterBuilder()
        .appendPattern("[d/M/yyyy HHmm]")
        .appendPattern("[d/M/yyyy]")
        .appendPattern("[yyyy-M-d HH:mm]")
        .appendPattern("[yyyy-M-d]")
        .appendPattern("[MMM d yyyy HH:mm]")
        .appendPattern("[MMM d yyyy]")
        .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
        .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
        .toFormatter(Locale.ENGLISH);

    public static final DateTimeFormatter TO_STRING_FORMATTER = DateTimeFormatter.ofPattern("yyyy-M-d HH:mm");

    public final LocalDateTime value;

    /**
     * Constructs a {@code Phone}.
     *
     * @param date A valid phone number.
     */
    public Date(String date) {
        requireNonNull(date);
        LocalDateTime dateTime = parseDate(date);
        checkArgument(dateTime != null, MESSAGE_CONSTRAINTS);
        value = dateTime;
    }

    /**
     * @param dateText
     * @return
     */
    public static LocalDateTime parseDate(String dateText) {
        try {
            return LocalDateTime.parse(dateText, INPUT_DATE_TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    @Override
    public String toString() {
        return value.format(TO_STRING_FORMATTER);
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
            || (other instanceof Date // instanceof handles nulls
            && value.equals(((Date) other).value)); // state check
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
