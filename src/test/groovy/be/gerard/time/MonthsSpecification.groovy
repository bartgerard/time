package be.gerard.time

import spock.lang.Specification

import java.time.YearMonth

import static be.gerard.time.DateRangeTestUtils.month
import static org.assertj.core.api.Assertions.assertThat

class MonthsSpecification extends Specification {

    def "months as ranges"() {
        given:
        final List<YearMonth> yearMonths = monthsAsString.collect { YearMonth.parse(it) }
        final List<DateRange> expectedRanges = DateRange.parse(expectedRangesAsStrings)

        when:
        final Set<DateRange> days = Months.asRanges(yearMonths)

        then:
        assertThat(days).containsExactlyInAnyOrderElementsOf(expectedRanges)

        where:
        monthsAsString                    | expectedRangesAsStrings                              | comment
        ["2000-01"]                       | ["2000-01-01..2000-01-31"]                           | ""
        ["2000-01", "2000-02"]            | ["2000-01-01..2000-02-29"]                           | ""
        ["2000-01", "2000-03"]            | ["2000-01-01..2000-01-31", "2000-03-01..2000-03-31"] | ""
        ["2000-01", "2000-02", "2000-03"] | ["2000-01-01..2000-03-31"]                           | ""

    }

    def "months within range"() {
        given:
        final DateRange range = DateRange.parse(rangeAsString)

        when:
        final List<YearMonth> months = Months.within(range)

        then:
        assertThat(months).containsExactlyElementsOf(expectedMonths)

        where:
        rangeAsString            | expectedMonths
        "2000-01-01"             | [month("2000-01")]
        "2000-01-01..2000-01-31" | [month("2000-01")]
        "2000-01-15..2000-01-31" | [month("2000-01")]
        "2000-01-01..2000-02-01" | [month("2000-01"), month("2000-02")]
        "2000-01-15..2000-02-01" | [month("2000-01"), month("2000-02")]
        "2000-01-15..2000-02-15" | [month("2000-01"), month("2000-02")]
        "2000-01-15..2000-02-29" | [month("2000-01"), month("2000-02")]
        "2000-01-01..2000-05-31" | [month("2000-01"), month("2000-02"), month("2000-03"), month("2000-04"), month("2000-05")]

    }

}
