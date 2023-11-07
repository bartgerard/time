package be.gerard.time;

import java.time.LocalDate;

public final class LocalDates {

    private LocalDates() {
        // no-op
    }

    public static LocalDate min(
            final LocalDate day1,
            final LocalDate day2
    ) {
        return day1.isBefore(day2) ? day1 : day2;
    }

    public static LocalDate max(
            final LocalDate day1,
            final LocalDate day2
    ) {
        return day1.isAfter(day2) ? day1 : day2;
    }

}
