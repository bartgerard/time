package be.gerard.time

import spock.lang.Specification

import java.time.LocalDate

import static org.assertj.core.api.Assertions.assertThat
import static org.assertj.core.api.Assertions.assertThatThrownBy

class DaysSpecification extends Specification {

    def "is consecutive"() {

        when:
        final boolean isConsecutive = Days.isConsecutive(Days.parse(days))

        then:
        isConsecutive == expectedConsecutiveness

        where:
        days                                                                   | expectedConsecutiveness | comment
        []                                                                     | true                    | ""
        ["2000-01-01"]                                                         | true                    | ""

        ["2000-01-01", "2000-01-02"]                                           | true                    | ""
        ["2000-01-01", "2000-01-02", "2000-01-03"]                             | true                    | ""
        ["2000-01-01", "2000-01-02", "2000-01-03", "2000-01-04"]               | true                    | ""
        ["2000-01-01", "2000-01-02", "2000-01-03", "2000-01-04", "2000-01-05"] | true                    | ""

        ["2000-01-02", "2000-01-01"]                                           | true                    | ""
        ["2000-01-03", "2000-01-01", "2000-01-02"]                             | true                    | ""
        ["2000-01-03", "2000-01-02", "2000-01-01"]                             | true                    | ""
        ["2000-01-04", "2000-01-01", "2000-01-02", "2000-01-03"]               | true                    | ""
        ["2000-01-04", "2000-01-03", "2000-01-01", "2000-01-02"]               | true                    | ""
        ["2000-01-04", "2000-01-03", "2000-01-02", "2000-01-01"]               | true                    | ""
        ["2000-01-05", "2000-01-01", "2000-01-02", "2000-01-03", "2000-01-04"] | true                    | ""
        ["2000-01-05", "2000-01-04", "2000-01-01", "2000-01-02", "2000-01-03"] | true                    | ""
        ["2000-01-05", "2000-01-04", "2000-01-03", "2000-01-01", "2000-01-02"] | true                    | ""
        ["2000-01-05", "2000-01-04", "2000-01-03", "2000-01-02", "2000-01-01"] | true                    | ""

        ["2000-01-01", "2000-01-03"]                                           | false                   | ""
        ["2000-01-03", "2000-01-01"]                                           | false                   | ""

        ["2000-01-01", "2000-01-03", "2000-01-04"]                             | false                   | ""
        ["2000-01-01", "2000-01-02", "2000-01-04"]                             | false                   | ""
        ["2000-01-04", "2000-01-01", "2000-01-03"]                             | false                   | ""
        ["2000-01-04", "2000-01-01", "2000-01-02"]                             | false                   | ""

    }

    def "days within range"() {
        given:
        final DateRange range = DateRange.parse(rangeAsString)
        final List<LocalDate> expectedDaysWithinRange = Days.parse(expectedDaysAsStrings)

        when:
        final List<LocalDate> days = Days.within(range)

        then:
        assertThat(days).containsExactlyElementsOf(expectedDaysWithinRange)

        where:
        rangeAsString            | expectedDaysAsStrings                      | comment
        "2000-01-01"             | ["2000-01-01"]                             | ""
        "2000-01-01..2000-01-02" | ["2000-01-01", "2000-01-02"]               | ""
        "2000-01-01..2000-01-03" | ["2000-01-01", "2000-01-02", "2000-01-03"] | ""

    }

    def "days within ranges"() {
        given:
        final List<DateRange> ranges = DateRange.parse(rangesAsStrings)
        final List<LocalDate> expectedDaysWithinRange = Days.parse(expectedDaysAsStrings)

        when:
        final List<LocalDate> days = Days.within(ranges)

        then:
        assertThat(days).containsExactlyElementsOf(expectedDaysWithinRange)

        where:
        rangesAsStrings                            | expectedDaysAsStrings                      | comment
        ["2000-01-01"]                             | ["2000-01-01"]                             | ""
        ["2000-01-01..2000-01-02"]                 | ["2000-01-01", "2000-01-02"]               | ""
        ["2000-01-01..2000-01-03"]                 | ["2000-01-01", "2000-01-02", "2000-01-03"] | ""
        ["2000-01-01", "2000-01-02", "2000-01-03"] | ["2000-01-01", "2000-01-02", "2000-01-03"] | ""
        ["2000-01-01..2000-01-02", "2000-01-03"]   | ["2000-01-01", "2000-01-02", "2000-01-03"] | ""
        ["2000-01-01", "2000-01-02..2000-01-03"]   | ["2000-01-01", "2000-01-02", "2000-01-03"] | ""

    }

    def "days within infinite range"() {
        expect:
        final DateRange range = DateRange.parse("2000-01-01..")
        assertThatThrownBy(() -> Days.within(range))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("infinite range to days conversion is not supported")
    }

    def "as timeline"() {
        given:
        final List<LocalDate> days = Days.parse(daysAsString)
        final List<DateRange> expectedTimeline = DateRange.parse(expectedTimelineAsString)

        when:
        final List<DateRange> timeline = Days.asTimeline(days)

        then:
        assertThat(timeline).containsExactlyElementsOf(expectedTimeline)

        where:
        daysAsString                               | expectedTimelineAsString                     | comment
        []                                         | []                                           | ""
        ["2000-01-01"]                             | ["2000-01-01.."]                             | ""
        ["2000-01-01", "2000-01-02"]               | ["2000-01-01", "2000-01-02.."]               | ""
        ["2000-01-01", "2000-01-02", "2000-01-03"] | ["2000-01-01", "2000-01-02", "2000-01-03.."] | ""

        ["2000-01-01", "2000-01-03"]               | ["2000-01-01..2000-01-02", "2000-01-03.."]   | ""

    }

}
