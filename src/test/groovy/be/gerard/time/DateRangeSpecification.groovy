package be.gerard.time

import spock.lang.Specification
import spock.lang.Title

import static be.gerard.time.DateRangeTestUtils.*
import static org.assertj.core.api.Assertions.assertThat

@Title("DateRange Tests")
class DateRangeSpecification extends Specification {

    def "length"() {

        when:
        final long length = dateRange.length()

        then:
        assertThat(length).isEqualTo(expectedLength)

        where:
        dateRange                           | expectedLength
        range1(["2000-01-01",])             | 1
        range1(["2000-01-01",])             | 1
        range(["2000-01-01", "2000-01-03"]) | 3
        range(["2000-01-01", "2000-01-04"]) | 4

    }

    def "contains day"() {

        when:
        final boolean contains = dateRange.containsDay(day)

        then:
        assertThat(contains).isEqualTo(isContained)

        where:
        dateRange                           | day               | isContained
        range1(["2000-01-01",])             | day("2000-01-01") | true
        range1(["2000-01-01",])             | day("2000-01-02") | false
        range(["2000-01-01", "2000-01-31"]) | day("2000-01-02") | true
        range(["2000-01-01", "2000-01-31"]) | day("2000-02-01") | false
        range(["2000-01-01",])              | day("2000-01-02") | true
        range(["2000-01-01",])              | day("1999-12-31") | false

    }

    def "intersects with"() {

        when:
        final boolean intersects = dateRange1.intersectsWith(dateRange2)

        then:
        assertThat(intersects).isEqualTo(isIntersecting)

        where:
        dateRange1                          | dateRange2                          | isIntersecting
        range(["2000-01-01",])              | range(["2000-01-01",])              | true
        range(["2000-01-01",])              | range(["2000-01-02",])              | true
        range1(["2000-01-01",])             | range1(["2000-01-02",])             | false
        range(["2000-01-01", "2000-01-10"]) | range(["2000-01-03", "2000-01-04"]) | true
        range(["2000-01-03", "2000-01-05"]) | range(["2000-01-01", "2000-01-10"]) | true
        range(["2000-01-01", "2000-01-05"]) | range(["2000-01-03", "2000-01-10"]) | true
        range(["2000-01-03", "2000-01-10"]) | range(["2000-01-01", "2000-01-05"]) | true

    }

    def "find used intersections"() {

        when:
        final List<DateRange> intersections = DateRange.findUsedIntersections(dateRanges)

        then:
        assertThat(intersections).containsExactlyElementsOf(expectedIntersections)

        where:
        dateRanges                                                                 | expectedIntersections                                                                                           | comment
        []                                                                         | []                                                                                                              | ""
        // INPUT:
        // |
        // EXPECTED:
        // |
        [range1(["2000-01-01",])]                                                  | [range1(["2000-01-01",])]                                                                                       | ""
        // INPUT:
        // |------------
        // EXPECTED:
        // |------------
        [range(["2000-01-01",])]                                                   | [range(["2000-01-01",])]                                                                                        | ""
        [range(["2000-01-01", "2000-01-02"])]                                      | [range(["2000-01-01", "2000-01-02"])]                                                                           | ""
        // INPUT:
        // |-|
        // EXPECTED:
        // |-|
        [range(["2000-01-01", "2000-01-07"])]                                      | [range(["2000-01-01", "2000-01-07"])]                                                                           | ""
        // INPUT:
        // |-------|
        // EXPECTED:
        // |-------|
        [range(["2000-01-01", "2000-01-07"])]                                      | [range(["2000-01-01", "2000-01-07"])]                                                                           | ""
        // INPUT:
        // |-------|
        //      |-------|
        // EXPECTED:
        // |----|--|----|
        [range(["2000-01-01", "2000-01-07"]), range(["2000-01-04", "2000-01-10"])] | [range(["2000-01-01", "2000-01-03"]), range(["2000-01-04", "2000-01-07"]), range(["2000-01-08", "2000-01-10"])] | ""
        // INPUT:
        // |------------|
        //      |--|
        // EXPECTED:
        // |----|--|----|
        [range(["2000-01-01", "2000-01-10"]), range(["2000-01-04", "2000-01-07"])] | [range(["2000-01-01", "2000-01-03"]), range(["2000-01-04", "2000-01-07"]), range(["2000-01-08", "2000-01-10"])] | ""
        // INPUT:
        // |----|
        //         |----|
        // EXPECTED:
        // |----|  |----|
        [range(["2000-01-01", "2000-01-04"]), range(["2000-01-07", "2000-01-10"])] | [range(["2000-01-01", "2000-01-04"]), range(["2000-01-07", "2000-01-10"])]                                      | ""
        // INPUT:
        // |----|
        //      |----|
        // EXPECTED:
        // |----|----|
        [range(["2000-01-01", "2000-01-04"]), range(["2000-01-04", "2000-01-07"])] | [range(["2000-01-01", "2000-01-03"]), range1(["2000-01-04",]), range(["2000-01-05", "2000-01-07"])]             | ""
        [range(["2022-04-04", "2022-04-05"]), range(["2022-04-05", "2022-04-06"])] | [range1(["2022-04-04"]), range1(["2022-04-05",]), range1(["2022-04-06",])]                                      | "overlap"

    }

    def "group subsequent days"() {

        when:
        final List<DateRange> groupSubsequentDays = DateRange.groupSubsequentDays(days)

        then:
        assertThat(groupSubsequentDays).containsExactlyElementsOf(expectedDateRanges)

        where:
        days                                                                                            | expectedDateRanges
        []                                                                                              | []
        [day("2000-01-01")]                                                                             | [range1(["2000-01-01",])]
        [day("2000-01-01"), day("2000-01-02")]                                                          | [range(["2000-01-01", "2000-01-02"])]
        [day("2000-01-01"), day("2000-01-03")]                                                          | [range1(["2000-01-01",]), range1(["2000-01-03",])]
        [day("2000-01-01"), day("2000-01-03"), day("2000-01-05")]                                       | [range1(["2000-01-01",]), range1(["2000-01-03",]), range1(["2000-01-05",])]
        [day("2000-01-01"), day("2000-01-02"), day("2000-01-03")]                                       | [range(["2000-01-01", "2000-01-03"])]
        [day("2000-01-01"), day("2000-01-02"), day("2000-01-04"), day("2000-01-05")]                    | [range(["2000-01-01", "2000-01-02"]), range(["2000-01-04", "2000-01-05"])]
        [day("2000-01-01"), day("2000-01-02"), day("2000-01-04"), day("2000-01-06"), day("2000-01-07")] | [range(["2000-01-01", "2000-01-02"]), range1(["2000-01-04",]), range(["2000-01-06", "2000-01-07"])]

    }

    def "merge"() {

        when:
        final List<DateRange> mergedDateRanges = DateRange.merge(dateRanges)

        then:
        assertThat(mergedDateRanges).containsExactlyElementsOf(expectedDateRanges)

        where:
        dateRanges                                                                 | expectedDateRanges                                                         | comment
        []                                                                         | []                                                                         | ""
        [range1(["2000-01-01",])]                                                  | [range1(["2000-01-01",])]                                                  | ""
        [range(["2000-01-01", "2000-01-03"])]                                      | [range(["2000-01-01", "2000-01-03"])]                                      | ""
        [range1(["2000-01-01",]), range1(["2000-01-02",])]                         | [range(["2000-01-01", "2000-01-02"])]                                      | ""
        [range1(["2000-01-01",]), range(["2000-01-02", "2000-01-03"])]             | [range(["2000-01-01", "2000-01-03"])]                                      | ""
        [range(["2000-01-01", "2000-01-02"]), range1(["2000-01-02",])]             | [range(["2000-01-01", "2000-01-02"])]                                      | ""
        [range(["2000-01-01", "2000-01-02"]), range(["2000-01-02", "2000-01-03"])] | [range(["2000-01-01", "2000-01-03"])]                                      | ""
        [range(["2000-01-01", "2000-01-02"]), range(["2000-01-03", "2000-01-04"])] | [range(["2000-01-01", "2000-01-04"])]                                      | ""
        [range(["2000-01-01", "2000-01-02"]), range(["2000-01-03", "2000-01-07"])] | [range(["2000-01-01", "2000-01-07"])]                                      | ""
        [range(["2000-01-01", "2000-01-02"]), range(["2000-01-04", "2000-01-07"])] | [range(["2000-01-01", "2000-01-02"]), range(["2000-01-04", "2000-01-07"])] | ""
        [range(["2000-01-01", "2000-01-04"]), range(["2000-01-03", "2000-01-07"])] | [range(["2000-01-01", "2000-01-07"])]                                      | "overlap"
        [range(["2000-01-01", "2000-01-04"]), range1(["2000-01-01",])]             | [range(["2000-01-01", "2000-01-04"])]                                      | "overlap"
        [range(["2000-01-01", "2000-01-04"]), range1(["2000-01-03",])]             | [range(["2000-01-01", "2000-01-04"])]                                      | "overlap"
        [range(["2000-01-01", "2000-01-04"]), range1(["2000-01-04",])]             | [range(["2000-01-01", "2000-01-04"])]                                      | ""

    }

    def "find all gaps"() {

        when:
        final List<DateRange> gaps = DateRange.findAllGaps(dateRanges)

        then:
        assertThat(gaps).containsExactlyInAnyOrderElementsOf(expectedGaps)

        where:
        dateRanges                                                                 | expectedGaps                          | comment
        []                                                                         | []                                    | ""
        [range1(["2000-01-01",])]                                                  | []                                    | "1 day"
        [range(["2000-01-01", "2000-01-31"])]                                      | []                                    | "1 month"
        [range(["2000-01-01"])]                                                    | []                                    | "non-finite"

        [range1(["2000-01-01",]), range1(["2000-01-03",])]                         | [range1(["2000-01-02",])]             | ""

        [range(["2000-01-01", "2000-01-31"]), range1(["2000-03-01",])]             | [range(["2000-02-01", "2000-02-29"])] | ""
        [range(["2000-01-01", "2000-01-31"]), range(["2000-03-01", "2000-03-31"])] | [range(["2000-02-01", "2000-02-29"])] | ""

        [range1(["2000-01-01",]), range(["2000-01-03",])]                          | [range1(["2000-01-02",])]             | ""
        [range(["2000-01-01", "2000-01-31"]), range(["2000-03-01"])]               | [range(["2000-02-01", "2000-02-29"])] | ""

        [range(["2000-01-01"]), range(["2001-01-01"])]                             | []                                    | "non-finite"

    }


}