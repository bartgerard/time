package be.gerard.time.api


import org.apache.commons.lang3.Validate

import java.time.LocalDate

class DateRangeTestUtils {

    static LocalDate day(
            final String dayAsString
    ) {
        return LocalDate.parse(dayAsString)
    }

    static DateRange range1(
            final List<String> rangeAsString
    ) {
        Validate.notEmpty(rangeAsString)

        return DateRange.ofOneDay(day(rangeAsString.get(0)))
    }

    static DateRange range(
            final List<String> rangeAsString
    ) {
        Validate.notEmpty(rangeAsString)

        if (rangeAsString.size() == 1) {
            return DateRange.startingOn(day(rangeAsString.get(0)))
        } else {
            return DateRange.of(day(rangeAsString.get(0)), day(rangeAsString.get(1)))
        }
    }

}
