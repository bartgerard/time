package be.gerard.time.internal;

import be.gerard.time.DateRange;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.notNull;

public record DateRangeEndInclusive(
        LocalDate startDate,
        LocalDate endDate
) implements DateRange {

    public DateRangeEndInclusive {
        notNull(startDate, "dateRange.startDate is invalid [null]");
        notNull(endDate, "dateRange.endDate is invalid [null]");

        isTrue(!startDate.isAfter(endDate), "dateRange.endDate should be after dateRange.startDate");
    }

    @Override
    public long length() {
        return ChronoUnit.DAYS.between(startDate(), endDate()) + 1;
    }

    @Override
    public boolean containsDay(final LocalDate day) {
        return !day.isBefore(startDate()) && !day.isAfter(endDate());
    }

    @Override
    public boolean intersectsWith(final DateRange otherRange) {
        return !otherRange.endDate().isBefore(this.startDate())
                && !otherRange.startDate().isAfter(this.endDate());
    }

    @Override
    public List<LocalDate> toDays() {
        return startDate().datesUntil(endDate().plusDays(1L)).toList();
    }

    @Override
    public String displayString() {
        return "[%s,%s]".formatted(startDate(), endDate());
    }

}
