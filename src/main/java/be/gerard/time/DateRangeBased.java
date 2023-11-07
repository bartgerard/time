package be.gerard.time;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.unmodifiableList;
import static java.util.Comparator.comparing;
import static java.util.Objects.isNull;

public interface DateRangeBased {

    static <T extends DateRangeBased> List<List<T>> groupOverlappingRanges(
            final List<T> rangeBasedItems
    ) {
        final List<T> rangeBasedItemsSorted = rangeBasedItems.stream()
                .sorted(comparing(DateRangeBased::range, comparing(DateRange::startDate).thenComparing(DateRange::endDate)))
                .toList();

        final List<List<T>> result = new ArrayList<>();
        List<T> current = null;
        DateRange currentRange = null;

        for (final T item : rangeBasedItemsSorted) {
            if (isNull(currentRange) || currentRange.endDate().isBefore(item.range().startDate())) {
                current = new ArrayList<>();
                result.add(unmodifiableList(current));
                currentRange = item.range();
            } else {
                currentRange = DateRange.of(currentRange.startDate(), LocalDates.max(currentRange.endDate(), item.range().endDate()));
            }

            current.add(item);
        }

        return unmodifiableList(result);
    }

    DateRange range();

}
