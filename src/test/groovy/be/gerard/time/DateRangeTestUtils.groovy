package be.gerard.time


import java.time.LocalDate
import java.time.YearMonth

import static org.apache.commons.lang3.Validate.notEmpty

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

    static DateRange range1(
            final List<String> rangeAsString
    ) {
        notEmpty(rangeAsString)

        return DateRange.ofOneDay(day(rangeAsString.get(0)))
    }

    static DateRange range(
            final List<String> rangeAsString
    ) {
        notEmpty(rangeAsString)

        if (rangeAsString.size() == 1) {
            return DateRange.startingOn(day(rangeAsString.get(0)))
        } else {
            return DateRange.of(day(rangeAsString.get(0)), day(rangeAsString.get(1)))
        }
    }

}
