package be.gerard.time.internal;

import be.gerard.time.DateRange;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import static java.time.temporal.TemporalAdjusters.firstDayOfYear;
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

    @Override
    public List<DateRange> splitByTemporalUnit(final TemporalUnit temporalUnit) {
        isTrue(temporalUnit.isDateBased(), "dateRange.splitByTemporalUnit only allows date based units");

        final LocalDate start = startDate().with(firstDayOfYear());

        final List<LocalDate> startDaysBetween = LongStream.iterate(0, i -> i + 1)
                .mapToObj(i -> start.plus(i, temporalUnit))
                .filter(day -> day.isAfter(startDate()))
                .takeWhile(day -> !day.isAfter(endDate()))
                .toList();

        if (startDaysBetween.isEmpty()) {
            return List.of(this);
        }

        final List<LocalDate> allStartDays = Stream.concat(
                        Stream.of(startDate()),
                        startDaysBetween.stream()
                )
                .toList();

        return Stream.concat(
                        IntStream.range(0, allStartDays.size() - 1)
                                .mapToObj(i -> DateRange.of(
                                        allStartDays.get(i),
                                        allStartDays.get(i + 1).minusDays(1L)
                                )),
                        Stream.of(DateRange.of(
                                allStartDays.get(allStartDays.size() - 1),
                                endDate()
                        ))
                )
                .toList();
    }

}
