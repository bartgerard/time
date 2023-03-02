package be.gerard.time.internal;

import be.gerard.time.DateRange;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.TemporalUnit;
import java.util.List;
import java.util.Set;

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
        return this.startDate.isEqual(day);
    }

    @Override
    public boolean containsRange(final DateRange range) {
        return this.startDate.isEqual(range.startDate())
                && this.startDate.isEqual(range.endDate());
    }

    @Override
    public boolean isIntersectingWith(final DateRange otherRange) {
        return otherRange.containsDay(this.startDate);
    }

    @Override
    public List<LocalDate> asDays() {
        return List.of(this.startDate);
    }

    @Override
    public List<YearMonth> asMonths() {
        return List.of(YearMonth.from(this.startDate));
    }

    @Override
    public String asText() {
        return "[%s]".formatted(this.startDate);
    }

    @Override
    public List<DateRange> splitByTemporalUnit(final TemporalUnit temporalUnit) {
        return List.of(this);
    }

    @Override
    public Set<DateRange> splitByDay(
            final LocalDate day
    ) {
        return Set.of(this);
    }

    @Override
    public Set<DateRange> splitByRange(final DateRange range) {
        return Set.of(this);
    }

}
