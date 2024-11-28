package be.gerard.time

import spock.lang.Specification
import spock.lang.Title

import static be.gerard.time.DateRangeTestUtils.day
import static be.gerard.time.DateRangeTestUtils.range
import static be.gerard.time.DateRangeTestUtils.ranges
import static org.assertj.core.api.Assertions.assertThat

@Title("DateRanges")
class DateRangesSpecification extends Specification {

    def "split by finiteness"() {

        given:
        DateRanges dateRanges = DateRanges.ofRanges(ranges(rangesAsStrings))

        when:
        Map<Boolean, List<DateRange>> split = dateRanges.splitByFiniteness()

        then:
        assertThat(split.get(true)).containsExactlyInAnyOrderElementsOf(ranges(expectedFiniteRanges))
        assertThat(split.get(false)).containsExactlyInAnyOrderElementsOf(ranges(expectedInfiniteRanges))

        where:
        rangesAsStrings                                          | expectedFiniteRanges                     | expectedInfiniteRanges           | comment
        ["2000-01-01"]                                           | ["2000-01-01"]                           | []                               | ""
        ["2000-01-01..2000-01-03"]                               | ["2000-01-01..2000-01-03"]               | []                               | ""

        ["2000-01-01.."]                                         | []                                       | ["2000-01-01.."]                 | ""
        ["2000-01-01..", "2001-01-01.."]                         | []                                       | ["2000-01-01..", "2001-01-01.."] | ""

        ["2000-01-01", "2000-01-01.."]                           | ["2000-01-01"]                           | ["2000-01-01.."]                 | ""
        ["2000-01-01..", "2000-01-01"]                           | ["2000-01-01"]                           | ["2000-01-01.."]                 | ""
        ["2000-01-01..2000-01-03", "2000-01-01.."]               | ["2000-01-01..2000-01-03"]               | ["2000-01-01.."]                 | ""
        ["2000-01-01", "2000-01-01..2000-01-03", "2000-01-01.."] | ["2000-01-01", "2000-01-01..2000-01-03"] | ["2000-01-01.."]                 | ""

    }

    def "split by before day"() {

        given:
        DateRanges dateRanges = DateRanges.ofRanges(ranges(rangesAsStrings))

        when:
        Map<Boolean, List<DateRange>> split = dateRanges.splitByBeforeDay(day(day))

        then:
        assertThat(split.get(true)).containsExactlyInAnyOrderElementsOf(ranges(expectedBeforeDayRanges))
        assertThat(split.get(false)).containsExactlyInAnyOrderElementsOf(ranges(expectedAfterOrEqualRanges))

        where:
        rangesAsStrings            | day          | expectedBeforeDayRanges    | expectedAfterOrEqualRanges | comment
        ["2000-01-02"]             | "2000-01-01" | []                         | ["2000-01-02"]             | ""
        ["2000-01-02"]             | "2000-01-02" | []                         | ["2000-01-02"]             | ""
        ["2000-01-02"]             | "2000-01-03" | ["2000-01-02"]             | []                         | ""

        ["2000-01-02..2000-01-04"] | "2000-01-01" | []                         | ["2000-01-02..2000-01-04"] | ""
        ["2000-01-02..2000-01-04"] | "2000-01-02" | []                         | ["2000-01-02..2000-01-04"] | ""
        ["2000-01-02..2000-01-04"] | "2000-01-03" | ["2000-01-02"]             | ["2000-01-03..2000-01-04"] | ""
        ["2000-01-02..2000-01-04"] | "2000-01-04" | ["2000-01-02..2000-01-03"] | ["2000-01-04"]             | ""
        ["2000-01-02..2000-01-04"] | "2000-01-05" | ["2000-01-02..2000-01-04"] | []                         | ""

        ["2000-01-02.."]           | "2000-01-01" | []                         | ["2000-01-02.."]           | ""
        ["2000-01-02.."]           | "2000-01-02" | []                         | ["2000-01-02.."]           | ""
        ["2000-01-02.."]           | "2000-01-03" | ["2000-01-02"]             | ["2000-01-03.."]           | ""

    }

    def "split by range"() {

        given:
        DateRanges dateRanges = DateRanges.ofRanges(ranges(rangesAsStrings))

        when:
        Map<String, List<DateRange>> split = dateRanges.splitByRange(range(rangeAsString))

        then:
        assertThat(split).containsExactlyInAnyOrderEntriesOf(expectedSplit)

        where:
        rangesAsStrings            | rangeAsString            | expectedSplit                                                                                                                         | comment
        ["2000-01-02"]             | "2000-01-01"             | Map.of("after", ranges(["2000-01-02"]))                                                                                               | ""
        ["2000-01-02"]             | "2000-01-02"             | Map.of("within", ranges(["2000-01-02"]))                                                                                              | ""
        ["2000-01-02"]             | "2000-01-03"             | Map.of("before", ranges(["2000-01-02"]))                                                                                              | ""

        ["2000-01-02..2000-01-04"] | "2000-01-01"             | Map.of("after", ranges(["2000-01-02..2000-01-04"]))                                                                                   | ""
        ["2000-01-02..2000-01-04"] | "2000-01-02"             | Map.of("within", ranges(["2000-01-02"]), "after", ranges(["2000-01-03..2000-01-04"]))                                                 | ""
        ["2000-01-02..2000-01-04"] | "2000-01-03"             | Map.of("before", ranges(["2000-01-02"]), "within", ranges(["2000-01-03"]), "after", ranges(["2000-01-04"]))                           | ""
        ["2000-01-02..2000-01-04"] | "2000-01-04"             | Map.of("before", ranges(["2000-01-02..2000-01-03"]), "within", ranges(["2000-01-04"]))                                                | ""
        ["2000-01-02..2000-01-04"] | "2000-01-05"             | Map.of("before", ranges(["2000-01-02..2000-01-04"]))                                                                                  | ""

        ["2000-01-02.."]           | "2000-01-01"             | Map.of("after", ranges(["2000-01-02.."]))                                                                                             | ""
        ["2000-01-02.."]           | "2000-01-02"             | Map.of("within", ranges(["2000-01-02"]), "after", ranges(["2000-01-03.."]))                                                           | ""
        ["2000-01-02.."]           | "2000-01-03"             | Map.of("before", ranges(["2000-01-02"]), "within", ranges(["2000-01-03"]), "after", ranges(["2000-01-04.."]))                         | ""
        ["2000-01-02.."]           | "2000-01-04"             | Map.of("before", ranges(["2000-01-02..2000-01-03"]), "within", ranges(["2000-01-04"]), "after", ranges(["2000-01-05.."]))             | ""

        ["2000-01-02"]             | "2000-01-01..2000-01-03" | Map.of("within", ranges(["2000-01-02"]))                                                                                              | ""
        ["2000-01-02"]             | "2000-01-02..2000-01-03" | Map.of("within", ranges(["2000-01-02"]))                                                                                              | ""
        ["2000-01-02"]             | "2000-01-03..2000-01-03" | Map.of("before", ranges(["2000-01-02"]))                                                                                              | ""

        ["2000-01-03..2000-01-06"] | "2000-01-01..2000-01-02" | Map.of("after", ranges(["2000-01-03..2000-01-06"]))                                                                                   | ""
        ["2000-01-03..2000-01-06"] | "2000-01-01..2000-01-03" | Map.of("within", ranges(["2000-01-03"]), "after", ranges(["2000-01-04..2000-01-06"]))                                                 | ""
        ["2000-01-03..2000-01-06"] | "2000-01-01..2000-01-04" | Map.of("within", ranges(["2000-01-03..2000-01-04"]), "after", ranges(["2000-01-05..2000-01-06"]))                                     | ""
        ["2000-01-03..2000-01-06"] | "2000-01-03..2000-01-04" | Map.of("within", ranges(["2000-01-03..2000-01-04"]), "after", ranges(["2000-01-05..2000-01-06"]))                                     | ""
        ["2000-01-03..2000-01-06"] | "2000-01-04..2000-01-05" | Map.of("before", ranges(["2000-01-03"]), "within", ranges(["2000-01-04..2000-01-05"]), "after", ranges(["2000-01-06"]))               | ""
        ["2000-01-03..2000-01-06"] | "2000-01-05..2000-01-06" | Map.of("before", ranges(["2000-01-03..2000-01-04"]), "within", ranges(["2000-01-05..2000-01-06"]))                                    | ""
        ["2000-01-03..2000-01-06"] | "2000-01-05..2000-01-08" | Map.of("before", ranges(["2000-01-03..2000-01-04"]), "within", ranges(["2000-01-05..2000-01-06"]))                                    | ""
        ["2000-01-03..2000-01-06"] | "2000-01-06..2000-01-08" | Map.of("before", ranges(["2000-01-03..2000-01-05"]), "within", ranges(["2000-01-06"]))                                                | ""
        ["2000-01-03..2000-01-06"] | "2000-01-07..2000-01-08" | Map.of("before", ranges(["2000-01-03..2000-01-06"]))                                                                                  | ""

        ["2000-01-03.."]           | "2000-01-01..2000-01-02" | Map.of("after", ranges(["2000-01-03.."]))                                                                                             | ""
        ["2000-01-03.."]           | "2000-01-02..2000-01-03" | Map.of("within", ranges(["2000-01-03"]), "after", ranges(["2000-01-04.."]))                                                           | ""
        ["2000-01-03.."]           | "2000-01-04..2000-01-05" | Map.of("before", ranges(["2000-01-03"]), "within", ranges(["2000-01-04..2000-01-05"]), "after", ranges(["2000-01-06.."]))             | ""
        ["2000-01-03.."]           | "2000-01-05..2000-01-06" | Map.of("before", ranges(["2000-01-03..2000-01-04"]), "within", ranges(["2000-01-05..2000-01-06"]), "after", ranges(["2000-01-07.."])) | ""

        ["2000-01-02"]             | "2000-01-01.."           | Map.of("within", ranges(["2000-01-02"]))                                                                                              | ""
        ["2000-01-02"]             | "2000-01-02.."           | Map.of("within", ranges(["2000-01-02"]))                                                                                              | ""
        ["2000-01-02"]             | "2000-01-03.."           | Map.of("before", ranges(["2000-01-02"]))                                                                                              | ""

        ["2000-01-03..2000-01-06"] | "2000-01-01.."           | Map.of("within", ranges(["2000-01-03..2000-01-06"]))                                                                                  | ""
        ["2000-01-03..2000-01-06"] | "2000-01-03.."           | Map.of("within", ranges(["2000-01-03..2000-01-06"]))                                                                                  | ""
        ["2000-01-03..2000-01-06"] | "2000-01-05.."           | Map.of("before", ranges(["2000-01-03..2000-01-04"]), "within", ranges(["2000-01-05..2000-01-06"]))                                    | ""
        ["2000-01-03..2000-01-06"] | "2000-01-06.."           | Map.of("before", ranges(["2000-01-03..2000-01-05"]), "within", ranges(["2000-01-06"]))                                                | ""
        ["2000-01-03..2000-01-06"] | "2000-01-07.."           | Map.of("before", ranges(["2000-01-03..2000-01-06"]))                                                                                  | ""

        ["2000-01-02.."]           | "2000-01-01.."           | Map.of("within", ranges(["2000-01-02.."]))                                                                                            | ""
        ["2000-01-02.."]           | "2000-01-02.."           | Map.of("within", ranges(["2000-01-02.."]))                                                                                            | ""
        ["2000-01-02.."]           | "2000-01-03.."           | Map.of("before", ranges(["2000-01-02"]), "within", ranges(["2000-01-03.."]))                                                          | ""

    }

}