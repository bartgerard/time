package be.gerard.time

import spock.lang.Specification
import spock.lang.Title

import static be.gerard.time.DateRangeTestUtils.ranges
import static org.assertj.core.api.Assertions.assertThat

@Title("DateRangeBased")
class DateRangeBasedSpecification extends Specification {

    def "group overlapping ranges"() {

        when:
        final List<List<DateRange>> overlappingRanges = DateRangeBased.groupOverlappingRanges(ranges(rangesAsStrings))

        then:
        assertThat(overlappingRanges).containsExactlyElementsOf(expectedOverlappingRanges)

        where:
        rangesAsStrings                                      | expectedOverlappingRanges                                                | comment
        ["2000-01-01"]                                       | [ranges(["2000-01-01"])]                                                 | ""
        ["2000-01-01..2000-01-03"]                           | [ranges(["2000-01-01..2000-01-03"])]                                     | ""
        ["2000-01-01.."]                                     | [ranges(["2000-01-01.."])]                                               | ""

        ["2000-01-01", "2000-01-01"]                         | [ranges(["2000-01-01", "2000-01-01"])]                                   | ""
        ["2000-01-01", "2000-01-02"]                         | [ranges(["2000-01-01"]), ranges(["2000-01-02"])]                         | ""
        ["2000-01-01", "2000-01-01", "2000-01-02"]           | [ranges(["2000-01-01", "2000-01-01"]), ranges(["2000-01-02"])]           | ""

        ["2000-01-01..2000-01-03", "2000-01-04..2000-01-06"] | [ranges(["2000-01-01..2000-01-03"]), ranges(["2000-01-04..2000-01-06"])] | ""
        ["2000-01-01..2000-01-03", "2000-01-05..2000-01-07"] | [ranges(["2000-01-01..2000-01-03"]), ranges(["2000-01-05..2000-01-07"])] | ""
        ["2000-01-01..2000-01-03", "2000-01-01..2000-01-06"] | [ranges(["2000-01-01..2000-01-03", "2000-01-01..2000-01-06"])]           | ""
        ["2000-01-01..2000-01-06", "2000-01-01..2000-01-03"] | [ranges(["2000-01-01..2000-01-03", "2000-01-01..2000-01-06"])]           | ""
        ["2000-01-02..2000-01-05", "2000-01-01..2000-01-06"] | [ranges(["2000-01-01..2000-01-06", "2000-01-02..2000-01-05"])]           | ""

        ["2000-01-01..", "2000-01-02.."]                     | [ranges(["2000-01-01..", "2000-01-02.."])]                               | ""
        ["2000-01-01..", "2000-01-03.."]                     | [ranges(["2000-01-01..", "2000-01-03.."])]                               | ""

        ["2000-01-01", "2000-01-01..2000-01-03"]             | [ranges(["2000-01-01", "2000-01-01..2000-01-03"])]                       | ""
        ["2000-01-01", "2000-01-02..2000-01-03"]             | [ranges(["2000-01-01"]), ranges(["2000-01-02..2000-01-03"])]             | ""

    }


}