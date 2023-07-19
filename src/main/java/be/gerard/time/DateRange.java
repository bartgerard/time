package be.gerard.time;

import be.gerard.time.internal.DateRangeEndInclusive;
import be.gerard.time.internal.DateRangeInfinite;
import be.gerard.time.internal.DateRangeOneDay;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.TemporalUnit;
import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.Comparator.comparing;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toUnmodifiableSet;
import static org.apache.commons.lang3.Validate.notNull;

public sealed interface DateRange extends DateRangeBased permits DateRangeOneDay, DateRangeEndInclusive, DateRangeInfinite {

    static DateRange of(
            final LocalDate startDate,
            final LocalDate endDate
    ) {
        if (startDate.isEqual(endDate)) {
            return ofOneDay(startDate);
        } else if (LocalDate.MAX.isEqual(endDate)) {
            return startingOn(startDate);
        } else {
            return withEndInclusive(startDate, endDate);
        }
    }

    static DateRange ofOneDay(
            final LocalDate day
    ) {
        return new DateRangeOneDay(day);
    }

    static DateRange startingOn(
            final LocalDate startDate
    ) {
        return new DateRangeInfinite(startDate);
    }

    private static DateRange withEndInclusive(
            final LocalDate startDate,
            final LocalDate endDateInclusive
    ) {
        return new DateRangeEndInclusive(
                startDate,
                endDateInclusive
        );
    }

    static List<DateRange> groupSubsequentDays(
            final Collection<LocalDate> days
    ) {
        final List<LocalDate> sortedDays = days.stream()
                .distinct()
                .sorted()
                .toList();

        if (sortedDays.isEmpty()) {
            return Collections.emptyList();
        } else if (sortedDays.size() == 1) {
            return List.of(ofOneDay(sortedDays.get(0)));
        }

        final int[] nonConsecutiveIndices = IntStream.range(1, sortedDays.size())
                .filter(i -> !sortedDays.get(i - 1).plusDays(1).isEqual(sortedDays.get(i)))
                .toArray();

        final int[] dateRangeBorders = IntStream.concat(
                        IntStream.of(0, sortedDays.size()),
                        Arrays.stream(nonConsecutiveIndices)
                )
                .sorted()
                .toArray();

        return IntStream.range(1, dateRangeBorders.length)
                .mapToObj(i -> DateRange.of(
                        sortedDays.get(dateRangeBorders[i - 1]),
                        sortedDays.get(dateRangeBorders[i] - 1)
                ))
                .toList();
    }

    private static LocalDate toExclusiveEndDate(
            final LocalDate date
    ) {
        return LocalDate.MAX.isEqual(date)
                ? LocalDate.MAX
                : date.plusDays(1L);
    }

    private static LocalDate toInclusiveEndDate(
            final LocalDate date
    ) {
        return LocalDate.MAX.isEqual(date)
                ? LocalDate.MAX
                : date.minusDays(1L);
    }

    static List<DateRange> findAllIntersections(
            final Collection<DateRange> ranges
    ) {
        if (ranges.isEmpty()) {
            return Collections.emptyList();
        }

        final List<LocalDate> borderDays = Stream.concat(
                        ranges.stream()
                                .map(DateRange::startDate),
                        ranges.stream()
                                .map(DateRange::endDate)
                                .map(DateRange::toExclusiveEndDate)
                )
                .distinct()
                .sorted()
                .toList();

        if (borderDays.size() == 1) {
            return List.of(DateRange.ofOneDay(borderDays.get(0)));
        }

        return IntStream.range(1, borderDays.size())
                .mapToObj(i -> DateRange.of(
                        borderDays.get(i - 1),
                        toInclusiveEndDate(borderDays.get(i))
                ))
                .toList();
    }

    static List<DateRange> findUsedIntersections(
            final Collection<DateRange> ranges
    ) {
        final List<DateRange> allIntersections = findAllIntersections(ranges);

        return allIntersections.stream()
                .filter(intersection -> ranges.stream()
                        .anyMatch(dateRange -> dateRange.containsDay(intersection.startDate()))
                )
                .toList();
    }

    static List<DateRange> findAllGaps(
            final Collection<DateRange> ranges
    ) {
        final List<DateRange> sortedIntersections = findUsedIntersections(ranges);

        if (sortedIntersections.isEmpty() || sortedIntersections.size() == 1) {
            return Collections.emptyList();
        }

        return IntStream.range(1, sortedIntersections.size())
                .filter(i -> !sortedIntersections.get(i - 1).endDate().plusDays(1L).isEqual(sortedIntersections.get(i).startDate()))
                .mapToObj(i -> DateRange.of(
                        sortedIntersections.get(i - 1).endDate().plusDays(1L),
                        sortedIntersections.get(i).startDate().minusDays(1L)
                ))
                .toList();
    }

    static List<DateRange> merge(
            final Collection<DateRange> dateRanges
    ) {
        final List<DateRange> sortedIntersections = findUsedIntersections(dateRanges);

        if (sortedIntersections.isEmpty()) {
            return Collections.emptyList();
        } else if (sortedIntersections.size() == 1) {
            return List.of(sortedIntersections.get(0));
        }

        final int[] nonConsecutiveIndices = IntStream.range(1, sortedIntersections.size())
                .filter(i -> !sortedIntersections.get(i - 1).endDate().plusDays(1L).isEqual(sortedIntersections.get(i).startDate()))
                .toArray();

        final int[] dateRangeBorders = IntStream.concat(
                        IntStream.of(0, sortedIntersections.size()),
                        Arrays.stream(nonConsecutiveIndices)
                )
                .sorted()
                .toArray();

        return IntStream.range(1, dateRangeBorders.length)
                .mapToObj(i -> DateRange.of(
                        sortedIntersections.get(dateRangeBorders[i - 1]).startDate(),
                        sortedIntersections.get(dateRangeBorders[i] - 1).endDate()
                ))
                .toList();
    }

    static List<LocalDate> asDays(
            final Collection<DateRange> ranges
    ) {
        return ranges.stream()
                .map(DateRange::asDays)
                .flatMap(List::stream)
                .toList();
    }

    static <T extends DateRangeBased> List<T> sort(
            final Collection<T> ranges
    ) {
        return ranges.stream()
                .sorted(comparing(
                        DateRangeBased::range,
                        comparing(DateRange::startDate).thenComparing(DateRange::endDate)
                ))
                .toList();
    }

    @Override
    default DateRange range() {
        return this;
    }

    LocalDate startDate();

    LocalDate endDate();

    default boolean isOneDay() {
        return false;
    }

    default boolean isFinite() {
        return true;
    }

    long length();

    boolean containsDay(
            LocalDate day
    );

    boolean containsRange(
            DateRange range
    );

    boolean isIntersectingWith(
            DateRange otherRange
    );

    default Optional<DateRange> intersect(
            final DateRange otherRange
    ) {
        if (!isIntersectingWith(otherRange)) {
            return Optional.empty();
        }

        return Optional.of(DateRange.of(
                LocalDates.max(startDate(), otherRange.startDate()),
                LocalDates.min(endDate(), otherRange.endDate())
        ));
    }

    List<LocalDate> asDays();

    List<YearMonth> asMonths();

    String asText();

    List<DateRange> splitByTemporalUnit(TemporalUnit temporalUnit);

    default Set<DateRange> splitByDay(
            final LocalDate day
    ) {
        notNull(day);

        if (containsDay(day) && !startDate().isEqual(day)) {
            return Set.of(
                    DateRange.of(startDate(), day.minusDays(1L)),
                    DateRange.of(day, endDate())
            );
        }

        return Set.of(this);
    }

    default Set<DateRange> splitByRange(
            final DateRange range
    ) {
        notNull(range);

        if (!isIntersectingWith(range) || range.containsRange(this)) {
            return Set.of(this);
        }

        return Stream.concat(
                        subtract(range).stream(),
                        intersect(range).stream()
                )
                .collect(toUnmodifiableSet());
    }

    default List<DateRange> subtract(final Collection<DateRange> subtrahends) {
        final List<DateRange> applicableSubtrahends = subtrahends.stream()
                .filter(this::isIntersectingWith)
                .toList();

        if (applicableSubtrahends.isEmpty()) {
            return singletonList(this);
        } else if (applicableSubtrahends.size() == 1) {
            return subtract(applicableSubtrahends.get(0));
        }

        final List<DateRange> innerGaps = findAllGaps(applicableSubtrahends)
                .stream()
                .filter(this::isIntersectingWith)
                .toList();

        final List<DateRange> outerGaps = subtract(DateRange.of(
                applicableSubtrahends.stream()
                        .map(DateRange::startDate)
                        .min(comparing(identity()))
                        .orElse(LocalDate.MIN),
                applicableSubtrahends.stream()
                        .map(DateRange::endDate)
                        .max(comparing(identity()))
                        .orElse(LocalDate.MAX)
        ));

        return Stream.concat(
                        innerGaps.stream(),
                        outerGaps.stream()
                )
                .sorted(comparing(DateRange::startDate)
                        .thenComparing(DateRange::endDate)
                )
                .toList();
    }

    default List<DateRange> subtract(final DateRange subtrahend) {
        notNull(subtrahend);

        if (equals(subtrahend)) {
            return emptyList();
        } else if (!isIntersectingWith(subtrahend)) {
            return singletonList(this);
        }

        return Stream.concat(
                        startDate().isBefore(subtrahend.startDate())
                                ? Stream.of(DateRange.of(startDate(), subtrahend.startDate().minusDays(1L)))
                                : Stream.empty(),
                        subtrahend.isFinite() && subtrahend.endDate().isBefore(endDate())
                                ? Stream.of(DateRange.of(subtrahend.endDate().plusDays(1L), endDate()))
                                : Stream.empty()
                )
                .toList();
    }

}
