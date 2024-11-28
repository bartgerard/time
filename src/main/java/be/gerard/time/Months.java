package be.gerard.time;

import java.time.YearMonth;
import java.util.Collection;
import java.util.List;
import java.util.stream.LongStream;

import static java.util.Objects.requireNonNull;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toUnmodifiableSet;
import static org.apache.commons.lang3.Validate.isTrue;

public final class Months {
    public Months() {
        // no-op
    }

    public static DateRange asRange(
            final YearMonth month
    ) {
        return DateRange.of(month.atDay(1), month.atEndOfMonth());
    }

    public static List<DateRange> asRanges(
            final Collection<YearMonth> months) {
        return months.stream()
                .distinct()
                .map(Months::asRange)
                .collect(collectingAndThen(
                        toUnmodifiableSet(),
                        DateRange::merge
                ));
    }

    public static List<YearMonth> within(
            final DateRange range
    ) {
        requireNonNull(range);
        isTrue(range.isFinite(), "infinite range to months conversion is not supported");

        final YearMonth startMonth = YearMonth.from(range.startDate());
        final YearMonth endMonth = YearMonth.from(range.endDate());
        return LongStream.iterate(0, i -> i + 1)
                .mapToObj(startMonth::plusMonths)
                .takeWhile(not(endMonth::isBefore))
                .toList();
    }

    public static List<YearMonth> within(
            final Collection<DateRange> ranges
    ) {
        requireNonNull(ranges);

        return ranges.stream()
                .map(Months::within)
                .flatMap(List::stream)
                .distinct()
                .sorted()
                .toList();
    }
}
