package be.gerard.time.model;

import org.apache.commons.lang3.Validate;
import org.springframework.util.Assert;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public record DateRange(
        LocalDate startDate,
        LocalDate endDate
) {

    public DateRange {
        Validate.notNull(startDate, "range.startDate is invalid [null]");
        Validate.notNull(endDate, "range.endDate is invalid [null]");

        Assert.isTrue(!startDate.isAfter(endDate), "range.endDate should be after range.startDate");
    }

    public static DateRange of(
            final LocalDate startDate,
            final LocalDate endDateNonInclusive
    ) {
        return new DateRange(startDate, endDateNonInclusive);
    }

    public static DateRange ofOneDay(final LocalDate day) {
        return of(day, day.plusDays(1));
    }

    public static DateRange startingOn(final LocalDate day) {
        return of(day, LocalDate.MAX);
    }

    public boolean contains(
            final LocalDate day
    ) {
        return !day.isBefore(startDate()) && day.isBefore(endDate());
    }

    public static Set<DateRange> toIntersections(
            final Collection<DateRange> dateRanges
    ) {
        final LocalDate[] borderDays = Stream.concat(
                        dateRanges.stream()
                                .map(DateRange::startDate),
                        dateRanges.stream()
                                .map(DateRange::endDate)
                )
                .distinct()
                .sorted()
                .toArray(LocalDate[]::new);

        if (borderDays.length == 1) {
            return Set.of(DateRange.ofOneDay(borderDays[0]));
        }

        final Set<DateRange> allIntersections = IntStream.range(1, borderDays.length)
                .mapToObj(i -> DateRange.of(borderDays[i - 1], borderDays[i]))
                .collect(Collectors.toUnmodifiableSet());

        return allIntersections.stream()
                .filter(intersection -> dateRanges.stream()
                        .anyMatch(dateRange -> dateRange.contains(intersection.startDate()))
                )
                .collect(Collectors.toUnmodifiableSet());
    }

    public static Set<DateRange> groupSubsequentDays(
            final Collection<LocalDate> days
    ) {
        final LocalDate[] sortedDays = days.stream()
                .distinct()
                .sorted()
                .toArray(LocalDate[]::new);


        if (sortedDays.length == 0) {
            return Collections.emptySet();
        } else if (sortedDays.length == 1) {
            return Set.of(DateRange.ofOneDay(sortedDays[0]));
        }

        final int[] nonConsecutiveIndices = IntStream.range(1, sortedDays.length)
                .filter(i -> !sortedDays[i - 1].plusDays(1).isEqual(sortedDays[i]))
                .toArray();

        final int[] dateRangeBorders = IntStream.concat(
                        IntStream.of(0, sortedDays.length),
                        Arrays.stream(nonConsecutiveIndices)
                )
                .sorted()
                .toArray();

        return IntStream.range(1, dateRangeBorders.length)
                .mapToObj(i -> DateRange.of(
                        sortedDays[dateRangeBorders[i - 1]],
                        sortedDays[dateRangeBorders[i] - 1].plusDays(1)
                ))
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public String toString() {
        return "[" + this.startDate + "," + this.endDate + "[";
    }
}
