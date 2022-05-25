package be.gerard.time.internal;

import be.gerard.time.DateRange;

import java.time.LocalDate;
import java.util.List;

import static org.apache.commons.lang3.Validate.notNull;

public record DateRangeOneDay(
        LocalDate startDate
) implements DateRange {

    public DateRangeOneDay {
        notNull(startDate, "dateRange.startDate is invalid [null]");
    }

    @Override
    public LocalDate endDate() {
        return startDate;
    }

    @Override
    public boolean isOneDay() {
        return true;
    }

    @Override
    public long length() {
        return 1;
    }

    @Override
    public boolean containsDay(final LocalDate day) {
        return startDate().isEqual(day);
    }

    @Override
    public boolean intersectsWith(final DateRange otherRange) {
        return otherRange.containsDay(startDate());
    }

    @Override
    public List<LocalDate> toDays() {
        return List.of(startDate());
    }

    @Override
    public String displayString() {
        return "[%s]".formatted(startDate());
    }

}
