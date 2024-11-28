package be.gerard.time;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjuster;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.time.DayOfWeek.MONDAY;
import static java.time.temporal.TemporalAdjusters.firstDayOfMonth;
import static java.time.temporal.TemporalAdjusters.ofDateAdjuster;
import static java.time.temporal.TemporalAdjusters.previousOrSame;
import static java.util.Collections.emptyList;
import static java.util.Comparator.naturalOrder;
import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.notEmpty;

public record Days(
        Set<LocalDate> dates
) {

    public Days(
            final Set<LocalDate> dates
    ) {
        notEmpty(dates);

        this.dates = Set.copyOf(dates);
    }

    static Days ofDays(
            final Set<LocalDate> days
    ) {
        return new Days(days);
    }

    static Days ofDays(
            final Collection<LocalDate> days
    ) {
        final Set<LocalDate> dates = Set.copyOf(days);
        return new Days(dates);
    }

    DateRanges asRanges() {
        return DateRanges.ofDays(dates);
    }

    /**
     * Parse zero or more dates represented in ISO into a List of LocalDates.
     *
     * @param values A Collection of Strings representing dates in ISO format.
     * @return A List of LocalDates.
     * @throws NullPointerException if values is null.
     */
    public static List<LocalDate> parse(
            final Collection<String> values
    ) {
        requireNonNull(values);

        return values.stream()
                .map(Days::parse)
                .toList();
    }

    /**
     * @throws NullPointerException if value is null.
     * @see LocalDate#parse(CharSequence)
     */
    public static LocalDate parse(
            final String value
    ) {
        requireNonNull(value);

        return LocalDate.parse(value);
    }

    /**
     * Return the first day from within the input.
     *
     * @param day1 A LocalDate.
     * @param day2 A LocalDate.
     * @return The first day from within the input.
     * @throws NullPointerException if day1 or day2 is null.
     */
    public static LocalDate min(
            final LocalDate day1,
            final LocalDate day2
    ) {
        requireNonNull(day1);
        requireNonNull(day2);

        return day1.isBefore(day2) ? day1 : day2;
    }

    /**
     * Return the last day from within the input.
     *
     * @param day1 A LocalDate.
     * @param day2 A LocalDate.
     * @return The last day from within the input.
     * @throws NullPointerException if day1 or day2 is null.
     */
    public static LocalDate max(
            final LocalDate day1,
            final LocalDate day2
    ) {
        requireNonNull(day1);
        requireNonNull(day2);

        return day1.isAfter(day2) ? day1 : day2;
    }

    /**
     * Return the first day of the given days.
     *
     * @param days A Collection of LocalDates.
     * @return The first day of the given days.
     * @throws NullPointerException if days is null.
     */
    public static Optional<LocalDate> min(
            final Collection<LocalDate> days
    ) {
        requireNonNull(days);

        return days.stream()
                .min(naturalOrder());
    }

    /**
     * Return the last day of the given days.
     *
     * @param days A Collection of LocalDates.
     * @return The last day of the given days.
     * @throws NullPointerException if days is null.
     */
    public static Optional<LocalDate> max(
            final Collection<LocalDate> days
    ) {
        requireNonNull(days);

        return days.stream()
                .max(naturalOrder());
    }

    /**
     * Evaluate if a list of days contains only consecutive days without gaps.
     *
     * @param days A Collection of LocalDates.
     * @return true if days contains only consecutive days without gaps.
     * @throws NullPointerException if days is null.
     */
    public static boolean isConsecutive(
            final Collection<LocalDate> days
    ) {
        requireNonNull(days);

        final List<LocalDate> orderedDays = days.stream()
                .sorted()
                .toList();

        return IntStream.range(1, orderedDays.size())
                .allMatch(i -> orderedDays.get(i - 1).plusDays(1L).isEqual(orderedDays.get(i)));
    }

    /**
     * Generate all days between a start day (included) and an end day (excluded).
     *
     * @param startInclusive A LocalDate representing the first day (included).
     * @param endExclusive   A LocalDate representing the last day (excluded).
     * @return A List of LocalDates between a start day (included) and an end day (excluded).
     */
    public static List<LocalDate> between(
            final LocalDate startInclusive,
            final LocalDate endExclusive
    ) {
        return startInclusive.datesUntil(endExclusive).toList();
    }

    /**
     * Convert a range into days.
     *
     * @param range A DateRange representing a moment in time.
     * @return A List of days contained within the given range.
     * @throws IllegalArgumentException if range is not finite.
     * @throws NullPointerException     if range is null.
     */
    public static List<LocalDate> within(
            final DateRange range
    ) {
        requireNonNull(range);
        isTrue(range.isFinite(), "infinite range to days conversion is not supported");

        return between(range.startDate(), DateRange.toExclusiveEndDate(range.endDate()));
    }

    /**
     * Convert multiple ranges into days.
     * The resulting days are ordered and without repetition.
     *
     * @param ranges A Collection of DateRanges representing multiple moments in time.
     * @return A List of days contained within these ranges.
     * @throws IllegalArgumentException if range is not finite.
     * @throws NullPointerException     if ranges is null.
     */
    public static List<LocalDate> within(
            final Collection<DateRange> ranges
    ) {
        requireNonNull(ranges);

        return ranges.stream()
                .map(Days::within)
                .flatMap(List::stream)
                .distinct()
                .sorted()
                .toList();
    }

    /**
     * Convert the given days into a continuous timeline where each day of the input represents the start of some persistent effect until the next day in the input or until the end of time.
     * The resulting ranges are ordered and without repetition.
     * <p>
     * Illustrated Example:
     * 2001-01-01 -> State A
     * 2001-05-01 -> State B
     * 2010-01-01 -> State C
     * <p>
     * State A will remain in effect until switched to State B. "2001-01-01..2001-04-30"
     * State B will remain in effect until switched to State X. "2001-05-01..2009-12-31"
     * State C will remain in effect until the end of time. "2010-01-01.."
     * <p>
     * Given: ["2001-01-01", "2001-05-01", "2010-01-01"]
     * Result: ["2001-01-01..2001-04-30", "2001-05-01..2009-12-31", "2010-01-01.."]
     * <p>
     * Note: The concept of State has been added to illustrate the usage, but is not part of the api.
     * Most probable logic is still required to connect the different states to the resulting ranges.
     * The correlation has been withheld from this api as to not limit the possibilities.
     *
     * @param days A Collection of LocalDates representing days that start a new range.
     * @return List of DateRanges representing a timeline.
     * @throws NullPointerException if days is null.
     */
    public static List<DateRange> asTimeline(final Collection<LocalDate> days) {
        requireNonNull(days);

        if (days.isEmpty()) {
            return emptyList();
        }

        final LocalDate[] borderDays = Stream.concat(
                        days.stream(),
                        Stream.of(LocalDate.MAX)
                )
                .distinct()
                .sorted()
                .toArray(LocalDate[]::new);

        if (borderDays.length == 1) {
            return List.of(DateRange.ofOneDay(borderDays[0]));
        }

        return IntStream.range(1, borderDays.length)
                .mapToObj(i -> DateRange.of(
                        borderDays[i - 1],
                        DateRange.toInclusiveEndDate(borderDays[i])
                ))
                .toList();
    }

    /**
     * Return an adjuster to find the start of a temporal unit.
     * <p>
     * DAYS      -> self,                        1951-02-11 -> 1951-02-11
     * WEEKS     -> first day of the week,       1951-02-11 -> 1951-02-05 (Monday)
     * MONTHS    -> first day of the month,      1951-02-11 -> 1951-02-01
     * YEARS     -> first day of the year,       1951-02-11 -> 1951-01-01
     * DECADES   -> first day of the decade,     1951-02-11 -> 1950-01-01
     * CENTURIES -> first day of the century,    1951-02-11 -> 1900-01-01
     * MILLENNIA -> first day of the millennium, 1951-02-11 -> 1000-01-01
     *
     * @param temporalUnit A ChronoUnit representing the temporal unit. (DAYS/WEEKS/MONTHS/YEARS/DECADES/CENTURIES/MILLENNIA)
     * @return The TemporalAdjuster to find the start of a temporal unit.
     */
    static TemporalAdjuster adjusterForStartOf(final ChronoUnit temporalUnit) {
        return switch (temporalUnit) {
            case DAYS -> ofDateAdjuster(day -> day);
            case WEEKS -> previousOrSame(MONDAY);
            case MONTHS -> firstDayOfMonth();
            case YEARS -> adjusterForStartOf(1);
            case DECADES -> adjusterForStartOf(10);
            case CENTURIES -> adjusterForStartOf(100);
            case MILLENNIA -> adjusterForStartOf(1000);
            default -> throw new IllegalArgumentException("Days.adjusterForStartOf(unit) does not support %s".formatted(temporalUnit));
        };
    }

    private static TemporalAdjuster adjusterForStartOf(final int years) {
        return ofDateAdjuster(day -> LocalDate.of((day.getYear() / years) * years, 1, 1));
    }

}
