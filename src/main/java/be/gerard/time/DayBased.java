package be.gerard.time;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static java.util.Map.Entry.comparingByKey;
import static java.util.stream.Collectors.groupingBy;

public interface DayBased {

    static <T extends DayBased> Map<LocalDate, List<T>> groupByDay(
            final List<T> dayBasedItems
    ) {
        return dayBasedItems.stream()
                .collect(groupingBy(DayBased::day));
    }

    static <T extends DayBased> List<List<T>> groupByDaySorted(
            final List<T> dayBasedItems
    ) {
        return groupByDay(dayBasedItems).entrySet()
                .stream()
                .sorted(comparingByKey())
                .map(Map.Entry::getValue)
                .toList();
    }

    LocalDate day();

}
