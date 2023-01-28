package be.gerard.time

import spock.lang.Specification
import spock.lang.Title

import static be.gerard.time.DateRangeTestUtils.range
import static be.gerard.time.DateRangeTestUtils.range1
import static org.assertj.core.api.Assertions.assertThat

@Title("DateRangeBased")
class DateRangeBasedSpecification extends Specification {

    def "group overlapping ranges"() {

        when:
        final List<List<DateRange>> overlappingRanges = DateRangeBased.groupOverlappingRanges(ranges)

        then:
        assertThat(overlappingRanges).containsExactlyElementsOf(expectedOverlappingRanges)

        where:
        ranges                                                                      | expectedOverlappingRanges                                                       | comment
        [range1(["2000-01-01",])]                                                   | [[range1(["2000-01-01",])]]                                                     | ""
        [range(["2000-01-01", "2000-01-03"])]                                       | [[range(["2000-01-01", "2000-01-03"])]]                                         | ""
        [range(["2000-01-01"])]                                                     | [[range(["2000-01-01"])]]                                                       | ""

        [range1(["2000-01-01",]), range1(["2000-01-01",])]                          | [[range1(["2000-01-01",]), range1(["2000-01-01",])]]                            | ""
        [range1(["2000-01-01",]), range1(["2000-01-02",])]                          | [[range1(["2000-01-01",])], [range1(["2000-01-02",])]]                          | ""
        [range1(["2000-01-01",]), range1(["2000-01-01",]), range1(["2000-01-02",])] | [[range1(["2000-01-01",]), range1(["2000-01-01",])], [range1(["2000-01-02",])]] | ""

        [range(["2000-01-01", "2000-01-03"]), range(["2000-01-04", "2000-01-06"])]  | [[range(["2000-01-01", "2000-01-03"])], [range(["2000-01-04", "2000-01-06"])]]  | ""
        [range(["2000-01-01", "2000-01-03"]), range(["2000-01-05", "2000-01-07"])]  | [[range(["2000-01-01", "2000-01-03"])], [range(["2000-01-05", "2000-01-07"])]]  | ""
        [range(["2000-01-01", "2000-01-03"]), range(["2000-01-01", "2000-01-06"])]  | [[range(["2000-01-01", "2000-01-03"]), range(["2000-01-01", "2000-01-06"])]]    | ""
        [range(["2000-01-01", "2000-01-06"]), range(["2000-01-01", "2000-01-03"])]  | [[range(["2000-01-01", "2000-01-03"]), range(["2000-01-01", "2000-01-06"])]]    | ""
        [range(["2000-01-02", "2000-01-05"]), range(["2000-01-01", "2000-01-06"])]  | [[range(["2000-01-01", "2000-01-06"]), range(["2000-01-02", "2000-01-05"])]]    | ""

        [range(["2000-01-01"]), range(["2000-01-02"])]                              | [[range(["2000-01-01"]), range(["2000-01-02"])]]                                | ""
        [range(["2000-01-01"]), range(["2000-01-03"])]                              | [[range(["2000-01-01"]), range(["2000-01-03"])]]                                | ""

        [range1(["2000-01-01",]), range(["2000-01-01", "2000-01-03"])]              | [[range1(["2000-01-01",]), range(["2000-01-01", "2000-01-03"])]]                | ""
        [range1(["2000-01-01",]), range(["2000-01-02", "2000-01-03"])]              | [[range1(["2000-01-01",])], [range(["2000-01-02", "2000-01-03"])]]              | ""

    }


}