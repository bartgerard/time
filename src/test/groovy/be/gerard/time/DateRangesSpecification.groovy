package be.gerard.time

import spock.lang.Specification
import spock.lang.Title

import static be.gerard.time.DateRangeTestUtils.*
import static org.assertj.core.api.Assertions.assertThat

@Title("DateRanges")
class DateRangesSpecification extends Specification {

    def "split by finiteness"() {

        given:
        DateRanges dateRanges = DateRanges.ofRanges(ranges)

        when:
        Map<Boolean, List<DateRange>> split = dateRanges.splitByFiniteness()

        then:
        assertThat(split.get(true)).containsExactlyInAnyOrderElementsOf(expectedFiniteRanges)
        assertThat(split.get(false)).containsExactlyInAnyOrderElementsOf(expectedInfiniteRanges)

        where:
        ranges                                                                                 | expectedFiniteRanges                                           | expectedInfiniteRanges                           | comment
        [range1(["2000-01-01",])]                                                              | [range1(["2000-01-01",])]                                      | []                                               | ""
        [range(["2000-01-01", "2000-01-03"])]                                                  | [range(["2000-01-01", "2000-01-03"])]                          | []                                               | ""

        [range(["2000-01-01",])]                                                               | []                                                             | [range(["2000-01-01",])]                         | ""
        [range(["2000-01-01",]), range(["2001-01-01",])]                                       | []                                                             | [range(["2000-01-01",]), range(["2001-01-01",])] | ""

        [range1(["2000-01-01",]), range(["2000-01-01",])]                                      | [range1(["2000-01-01",])]                                      | [range(["2000-01-01",])]                         | ""
        [range(["2000-01-01",]), range1(["2000-01-01",])]                                      | [range1(["2000-01-01",])]                                      | [range(["2000-01-01",])]                         | ""
        [range(["2000-01-01", "2000-01-03"]), range(["2000-01-01",])]                          | [range(["2000-01-01", "2000-01-03"])]                          | [range(["2000-01-01",])]                         | ""
        [range1(["2000-01-01",]), range(["2000-01-01", "2000-01-03"]), range(["2000-01-01",])] | [range1(["2000-01-01",]), range(["2000-01-01", "2000-01-03"])] | [range(["2000-01-01",])]                         | ""

    }

    def "split by before day"() {

        given:
        DateRanges dateRanges = DateRanges.ofRanges(ranges)

        when:
        Map<Boolean, List<DateRange>> split = dateRanges.splitByBeforeDay(day(day))

        then:
        assertThat(split.get(true)).containsExactlyInAnyOrderElementsOf(expectedBeforeDayRanges)
        assertThat(split.get(false)).containsExactlyInAnyOrderElementsOf(expectedAfterOrEqualRanges)

        where:
        ranges                                | day          | expectedBeforeDayRanges               | expectedAfterOrEqualRanges            | comment
        [range1(["2000-01-02",])]             | "2000-01-01" | []                                    | [range1(["2000-01-02",])]             | ""
        [range1(["2000-01-02",])]             | "2000-01-02" | []                                    | [range1(["2000-01-02",])]             | ""
        [range1(["2000-01-02",])]             | "2000-01-03" | [range1(["2000-01-02",])]             | []                                    | ""

        [range(["2000-01-02", "2000-01-04"])] | "2000-01-01" | []                                    | [range(["2000-01-02", "2000-01-04"])] | ""
        [range(["2000-01-02", "2000-01-04"])] | "2000-01-02" | []                                    | [range(["2000-01-02", "2000-01-04"])] | ""
        [range(["2000-01-02", "2000-01-04"])] | "2000-01-03" | [range1(["2000-01-02",])]             | [range(["2000-01-03", "2000-01-04"])] | ""
        [range(["2000-01-02", "2000-01-04"])] | "2000-01-04" | [range(["2000-01-02", "2000-01-03"])] | [range1(["2000-01-04",])]             | ""
        [range(["2000-01-02", "2000-01-04"])] | "2000-01-05" | [range(["2000-01-02", "2000-01-04"])] | []                                    | ""

        [range(["2000-01-02",])]              | "2000-01-01" | []                                    | [range(["2000-01-02",])]              | ""
        [range(["2000-01-02",])]              | "2000-01-02" | []                                    | [range(["2000-01-02",])]              | ""
        [range(["2000-01-02",])]              | "2000-01-03" | [range1(["2000-01-02",])]             | [range(["2000-01-03",])]              | ""

    }

    def "split by range"() {

        given:
        DateRanges dateRanges = DateRanges.ofRanges(ranges)

        when:
        Map<String, List<DateRange>> split = dateRanges.splitByRange(range)

        then:
        assertThat(split).containsExactlyInAnyOrderEntriesOf(expectedSplit)

        where:
        ranges                                | range                               | expectedSplit                                                                                                                               | comment
        [range1(["2000-01-02",])]             | range1(["2000-01-01",])             | Map.of("after", [range1(["2000-01-02",])])                                                                                                  | ""
        [range1(["2000-01-02",])]             | range1(["2000-01-02",])             | Map.of("within", [range1(["2000-01-02",])])                                                                                                 | ""
        [range1(["2000-01-02",])]             | range1(["2000-01-03",])             | Map.of("before", [range1(["2000-01-02",]),])                                                                                                | ""

        [range(["2000-01-02", "2000-01-04"])] | range1(["2000-01-01",])             | Map.of("after", [range(["2000-01-02", "2000-01-04"])])                                                                                      | ""
        [range(["2000-01-02", "2000-01-04"])] | range1(["2000-01-02",])             | Map.of("within", [range1(["2000-01-02",]),], "after", [range(["2000-01-03", "2000-01-04"])])                                                | ""
        [range(["2000-01-02", "2000-01-04"])] | range1(["2000-01-03",])             | Map.of("before", [range1(["2000-01-02",])], "within", [range1(["2000-01-03",])], "after", [range1(["2000-01-04",])])                        | ""
        [range(["2000-01-02", "2000-01-04"])] | range1(["2000-01-04",])             | Map.of("before", [range(["2000-01-02", "2000-01-03"])], "within", [range1(["2000-01-04",])])                                                | ""
        [range(["2000-01-02", "2000-01-04"])] | range1(["2000-01-05",])             | Map.of("before", [range(["2000-01-02", "2000-01-04"]),])                                                                                    | ""

        [range(["2000-01-02",])]              | range1(["2000-01-01",])             | Map.of("after", [range(["2000-01-02",])])                                                                                                   | ""
        [range(["2000-01-02",])]              | range1(["2000-01-02",])             | Map.of("within", [range1(["2000-01-02",])], "after", [range(["2000-01-03",])])                                                              | ""
        [range(["2000-01-02",])]              | range1(["2000-01-03",])             | Map.of("before", [range1(["2000-01-02",])], "within", [range1(["2000-01-03",])], "after", [range(["2000-01-04",])])                         | ""
        [range(["2000-01-02",])]              | range1(["2000-01-04",])             | Map.of("before", [range(["2000-01-02", "2000-01-03"])], "within", [range1(["2000-01-04",])], "after", [range(["2000-01-05",])])             | ""

        [range1(["2000-01-02",])]             | range(["2000-01-01", "2000-01-03"]) | Map.of("within", [range1(["2000-01-02",])])                                                                                                 | ""
        [range1(["2000-01-02",])]             | range(["2000-01-02", "2000-01-03"]) | Map.of("within", [range1(["2000-01-02",])])                                                                                                 | ""
        [range1(["2000-01-02",])]             | range(["2000-01-03", "2000-01-03"]) | Map.of("before", [range1(["2000-01-02",]),])                                                                                                | ""

        [range(["2000-01-03", "2000-01-06"])] | range(["2000-01-01", "2000-01-02"]) | Map.of("after", [range(["2000-01-03", "2000-01-06"])])                                                                                      | ""
        [range(["2000-01-03", "2000-01-06"])] | range(["2000-01-01", "2000-01-03"]) | Map.of("within", [range1(["2000-01-03",])], "after", [range(["2000-01-04", "2000-01-06"])])                                                 | ""
        [range(["2000-01-03", "2000-01-06"])] | range(["2000-01-01", "2000-01-04"]) | Map.of("within", [range(["2000-01-03", "2000-01-04"])], "after", [range(["2000-01-05", "2000-01-06"])])                                     | ""
        [range(["2000-01-03", "2000-01-06"])] | range(["2000-01-03", "2000-01-04"]) | Map.of("within", [range(["2000-01-03", "2000-01-04"])], "after", [range(["2000-01-05", "2000-01-06"])])                                     | ""
        [range(["2000-01-03", "2000-01-06"])] | range(["2000-01-04", "2000-01-05"]) | Map.of("before", [range1(["2000-01-03",])], "within", [range(["2000-01-04", "2000-01-05"])], "after", [range1(["2000-01-06",])])            | ""
        [range(["2000-01-03", "2000-01-06"])] | range(["2000-01-05", "2000-01-06"]) | Map.of("before", [range(["2000-01-03", "2000-01-04"])], "within", [range(["2000-01-05", "2000-01-06"])])                                    | ""
        [range(["2000-01-03", "2000-01-06"])] | range(["2000-01-05", "2000-01-08"]) | Map.of("before", [range(["2000-01-03", "2000-01-04"])], "within", [range(["2000-01-05", "2000-01-06"])])                                    | ""
        [range(["2000-01-03", "2000-01-06"])] | range(["2000-01-06", "2000-01-08"]) | Map.of("before", [range(["2000-01-03", "2000-01-05"])], "within", [range1(["2000-01-06",])])                                                | ""
        [range(["2000-01-03", "2000-01-06"])] | range(["2000-01-07", "2000-01-08"]) | Map.of("before", [range(["2000-01-03", "2000-01-06"])])                                                                                     | ""

        [range(["2000-01-03",])]              | range(["2000-01-01", "2000-01-02"]) | Map.of("after", [range(["2000-01-03",])])                                                                                                   | ""
        [range(["2000-01-03",])]              | range(["2000-01-02", "2000-01-03"]) | Map.of("within", [range1(["2000-01-03",])], "after", [range(["2000-01-04",])])                                                              | ""
        [range(["2000-01-03",])]              | range(["2000-01-04", "2000-01-05"]) | Map.of("before", [range1(["2000-01-03",])], "within", [range(["2000-01-04", "2000-01-05"])], "after", [range(["2000-01-06",])])             | ""
        [range(["2000-01-03",])]              | range(["2000-01-05", "2000-01-06"]) | Map.of("before", [range(["2000-01-03", "2000-01-04"])], "within", [range(["2000-01-05", "2000-01-06"])], "after", [range(["2000-01-07",])]) | ""

        [range1(["2000-01-02",])]             | range(["2000-01-01"])               | Map.of("within", [range1(["2000-01-02",])])                                                                                                 | ""
        [range1(["2000-01-02",])]             | range(["2000-01-02"])               | Map.of("within", [range1(["2000-01-02",])])                                                                                                 | ""
        [range1(["2000-01-02",])]             | range(["2000-01-03"])               | Map.of("before", [range1(["2000-01-02",]),])                                                                                                | ""

        [range(["2000-01-03", "2000-01-06"])] | range(["2000-01-01",])              | Map.of("within", [range(["2000-01-03", "2000-01-06"])])                                                                                     | ""
        [range(["2000-01-03", "2000-01-06"])] | range(["2000-01-03",])              | Map.of("within", [range(["2000-01-03", "2000-01-06"])])                                                                                     | ""
        [range(["2000-01-03", "2000-01-06"])] | range(["2000-01-05",])              | Map.of("before", [range(["2000-01-03", "2000-01-04"])], "within", [range(["2000-01-05", "2000-01-06"])])                                    | ""
        [range(["2000-01-03", "2000-01-06"])] | range(["2000-01-06",])              | Map.of("before", [range(["2000-01-03", "2000-01-05"])], "within", [range1(["2000-01-06",])])                                                | ""
        [range(["2000-01-03", "2000-01-06"])] | range(["2000-01-07",])              | Map.of("before", [range(["2000-01-03", "2000-01-06"])])                                                                                     | ""

        [range(["2000-01-02",])]              | range(["2000-01-01"])               | Map.of("within", [range(["2000-01-02",])])                                                                                                  | ""
        [range(["2000-01-02",])]              | range(["2000-01-02"])               | Map.of("within", [range(["2000-01-02",])])                                                                                                  | ""
        [range(["2000-01-02",])]              | range(["2000-01-03"])               | Map.of("before", [range1(["2000-01-02",])], "within", [range(["2000-01-03",]),])                                                            | ""

    }

}