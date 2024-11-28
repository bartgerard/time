package be.gerard.time.internal;

import be.gerard.time.DateRange;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import static java.time.temporal.TemporalAdjusters.firstDayOfYear;
import static java.util.Objects.requireNonNull;
import static java.util.function.Predicate.not;
import static org.apache.commons.lang3.Validate.isTrue;

public record DateRangeEndInclusive(
        LocalDate startDate,
        LocalDate endDate
) implements DateRange {

    public DateRangeEndInclusive {
        requireNonNull(startDate, "dateRange.startDate is invalid [null]");
        requireNonNull(endDate, "dateRange.endDate is invalid [null]");

        isTrue(!startDate.isAfter(endDate), "dateRange.endDate should be after dateRange.startDate");
    }

    @Override
    public long length() {
        return ChronoUnit.DAYS.between(this.startDate, this.endDate) + 1;
    }

    @Override
    public boolean containsDay(final LocalDate day) {
        return !day.isBefore(this.startDate) && !day.isAfter(this.endDate);
    }

    @Override
    public boolean containsRange(final DateRange range) {
        return !this.startDate.isAfter(range.startDate())
                && !this.endDate.isBefore(range.endDate());
    }

    @Override
    public boolean isIntersectingWith(final DateRange otherRange) {
        return !otherRange.endDate().isBefore(this.startDate)
                && !otherRange.startDate().isAfter(this.endDate);
    }

    @Override
    public List<LocalDate> asDays() {
        return this.startDate.datesUntil(endDate().plusDays(1L)).toList();
    }

    @Override
    public List<YearMonth> asMonths() {
        final YearMonth startMonth = YearMonth.from(this.startDate);
        final YearMonth endMonth = YearMonth.from(this.endDate);
        return LongStream.iterate(0, i -> i + 1)
                .mapToObj(startMonth::plusMonths)
                .takeWhile(not(endMonth::isBefore))
                .toList();
    }

    @Override
    public String asText() {
        return "%s..%s".formatted(this.startDate, this.endDate);
    }

    @Override
    public List<DateRange> splitByTemporalUnit(final TemporalUnit temporalUnit) {
        isTrue(temporalUnit.isDateBased(), "dateRange.splitByTemporalUnit only allows date based units");

        final LocalDate start = this.startDate.with(firstDayOfYear());

        final List<LocalDate> startDaysBetween = LongStream.iterate(0, i -> i + 1)
                .mapToObj(i -> start.plus(i, temporalUnit))
                .filter(day -> day.isAfter(this.startDate))
                .takeWhile(day -> !day.isAfter(this.endDate))
                .toList();

        if (startDaysBetween.isEmpty()) {
            return List.of(this);
        }

        final List<LocalDate> allStartDays = Stream.concat(
                        Stream.of(this.startDate),
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
                                allStartDays.getLast(),
                                this.endDate
                        ))
                )
                .toList();
    }

}
