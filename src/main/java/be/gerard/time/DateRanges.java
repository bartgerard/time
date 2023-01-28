package be.gerard.time;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.partitioningBy;
import static java.util.stream.Collectors.toUnmodifiableList;
import static org.apache.commons.lang3.Validate.notEmpty;

public record DateRanges(
        List<DateRange> ranges
) {

    public DateRanges(
            final List<DateRange> ranges
    ) {
        notEmpty(ranges);

        this.ranges = ranges.stream()
                .sorted(comparing(DateRange::startDate).thenComparing(DateRange::endDate))
                .toList();
    }

    static DateRanges ofRanges(
            final List<DateRange> ranges
    ) {
        return new DateRanges(ranges);
    }

    static DateRanges ofRanges(
            final Collection<DateRange> ranges
    ) {
        return new DateRanges(
                List.copyOf(ranges)
        );
    }

    static List<DateRanges> ofSplitRanges(
            final Map<?, List<DateRange>> split
    ) {
        return split.values()
                .stream()
                .map(DateRanges::ofRanges)
                .toList();
    }

    static DateRanges ofDays(
            final Collection<LocalDate> days
    ) {
        return new DateRanges(
                DateRange.groupSubsequentDays(days)
        );
    }

    Days asDays() {
        return Days.ofDays(
                DateRange.asDays(ranges)
        );
    }

    Map<Boolean, List<DateRange>> splitByFiniteness() {
        return ranges.stream()
                .collect(partitioningBy(
                        DateRange::isFinite,
                        toUnmodifiableList()
                ));
    }

    private Stream<DateRange> splitOnDay(
            final LocalDate day
    ) {
        return ranges.stream()
                .flatMap(range -> range.containsDay(day)
                        ? range.splitByDay(day).stream()
                        : Stream.of(range)
                );
    }

    Map<Boolean, List<DateRange>> splitByBeforeDay(
            final LocalDate day
    ) {
        return splitOnDay(day)
                .collect(partitioningBy(
                        range -> range.endDate().isBefore(day),
                        toUnmodifiableList()
                ));
    }

    private Stream<DateRange> splitOnRange(
            final DateRange otherRange
    ) {
        return ranges.stream()
                .flatMap(range -> range.isIntersectingWith(otherRange)
                        ? range.splitByRange(otherRange).stream()
                        : Stream.of(range)
                );
    }

    Map<String, List<DateRange>> splitByRange(
            final DateRange otherRange
    ) {
        return splitOnRange(otherRange)
                .collect(groupingBy(
                        range -> {
                            if (range.endDate().isBefore(otherRange.startDate())) {
                                return "before";
                            } else if (range.startDate().isAfter(otherRange.endDate())) {
                                return "after";
                            } else {
                                return "within";
                            }
                        },
                        toUnmodifiableList()
                ));
    }

}
