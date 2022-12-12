package be.gerard.time.internal;

import be.gerard.time.DateRange;

import java.time.LocalDate;
import java.time.temporal.TemporalUnit;
import java.util.List;

import static org.apache.commons.lang3.Validate.notNull;

public record DateRangeInfinite(
        LocalDate startDate
) implements DateRange {

    public DateRangeInfinite {
        notNull(startDate, "dateRange.startDate is invalid [null]");
    }

    @Override
    public LocalDate endDate() {
        return LocalDate.MAX;
    }

    @Override
    public boolean isFinite() {
        return false;
    }

    @Override
    public long length() {
        return Long.MAX_VALUE;
    }

    @Override
    public boolean containsDay(final LocalDate day) {
        return !day.isBefore(startDate());
    }

    @Override
    public boolean intersectsWith(final DateRange otherRange) {
        return !this.startDate().isAfter(otherRange.endDate());
    }

    @Override
    public List<LocalDate> toDays() {
        throw new UnsupportedOperationException("dateRange.toDays() can not be applied to infinite ranges");
    }

    @Override
    public String displayString() {
        return "[%s,[".formatted(startDate());
    }

    @Override
    public List<DateRange> splitByTemporalUnit(final TemporalUnit temporalUnit) {
        throw new UnsupportedOperationException("dateRange.splitByTemporalUnit(temporalUnit) can not be applied to infinite ranges");
    }

}
