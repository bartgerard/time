package be.gerard.time

import java.time.LocalDate
import java.time.YearMonth

class DateRangeTestUtils {

    static LocalDate day(
            final String dayAsString
    ) {
        return LocalDate.parse(dayAsString)
    }

    static YearMonth month(
            final String monthAsString
    ) {
        return YearMonth.parse(monthAsString)
    }

    static DateRange range(
            final String rangeAsString
    ) {
        return DateRange.parse(rangeAsString)
    }

    static List<DateRange> ranges(
            final List<String> rangesAsString
    ) {
        return rangesAsString.stream()
                .map { range(it) }
                .toList()
    }

}
