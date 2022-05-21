package be.gerard.time.api

import org.assertj.core.api.Assertions
import spock.lang.Specification
import spock.lang.Title

import java.time.LocalDate

import static be.gerard.time.api.DateRangeTestUtils.*

@Title("DateRange Tests")
class DateRangeSpecification extends Specification {

    def "length"() {
        //given:

        when:
        final long length = dateRange.length()

        then:
        Assertions.assertThat(length).isEqualTo(expectedLength)

        where:
        dateRange                           | expectedLength
        range1(["2000-01-01",])             | 1
        range1(["2000-01-01",])             | 1
        range(["2000-01-01", "2000-01-03"]) | 2
        range(["2000-01-01", "2000-01-04"]) | 3

    }

    def "contains"() {
        //given:

        when:
        final boolean contains = dateRange.contains(day)

        then:
        Assertions.assertThat(contains).isEqualTo(isContained)

        where:
        dateRange               | day               | isContained
        range1(["2000-01-01",]) | day("2000-01-01") | true
        range1(["2000-01-01",]) | day("2000-01-02") | false
        range(["2000-01-01",])  | day("2000-01-02") | true
        range(["2000-01-01",])  | day("1999-12-31") | false

    }

    def "overlaps"() {
        //given:

        when:
        final boolean overlapping = dateRange1.overlaps(dateRange2)

        then:
        Assertions.assertThat(overlapping).isEqualTo(isOverlapping)

        where:
        dateRange1                          | dateRange2                          | isOverlapping
        range(["2000-01-01",])              | range(["2000-01-01",])              | true
        range(["2000-01-01",])              | range(["2000-01-02",])              | true
        range1(["2000-01-01",])             | range1(["2000-01-02",])             | false
        range(["2000-01-01", "2000-01-10"]) | range(["2000-01-03", "2000-01-04"]) | true
        range(["2000-01-03", "2000-01-05"]) | range(["2000-01-01", "2000-01-10"]) | true
        range(["2000-01-01", "2000-01-05"]) | range(["2000-01-03", "2000-01-10"]) | true
        range(["2000-01-03", "2000-01-10"]) | range(["2000-01-01", "2000-01-05"]) | true

    }

    def "toIntersections"() {
        //given:

        when:
        final Set<DateRange> intersections = DateRange.toIntersections(dateRanges)

        then:
        Assertions.assertThat(intersections).containsExactlyInAnyOrderElementsOf(expectedIntersections)

        where:
        dateRanges                                                                        | expectedIntersections                                                                                                 | comment
        Collections.<DateRange> emptyList()                                               | Collections.<DateRange> emptySet()                                                                                    | ""
        // INPUT:
        // |
        // EXPECTED:
        // |
        List.of(range1(["2000-01-01",]))                                                  | Set.of(range1(["2000-01-01",]))                                                                                       | ""
        // INPUT:
        // |------------
        // EXPECTED:
        // |------------
        List.of(range(["2000-01-01",]))                                                   | Set.of(range(["2000-01-01",]))                                                                                        | ""
        List.of(range(["2000-01-01", "2000-01-02"]))                                      | Set.of(range(["2000-01-01", "2000-01-02"]))                                                                           | ""
        // INPUT:
        // |-|
        // EXPECTED:
        // |-|
        List.of(range(["2000-01-01", "2000-01-07"]))                                      | Set.of(range(["2000-01-01", "2000-01-07"]))                                                                           | ""
        // INPUT:
        // |-------|
        // EXPECTED:
        // |-------|
        List.of(range(["2000-01-01", "2000-01-07"]))                                      | Set.of(range(["2000-01-01", "2000-01-07"]))                                                                           | ""
        // INPUT:
        // |-------|
        //      |-------|
        // EXPECTED:
        // |----|--|----|
        List.of(range(["2000-01-01", "2000-01-07"]), range(["2000-01-04", "2000-01-10"])) | Set.of(range(["2000-01-01", "2000-01-04"]), range(["2000-01-04", "2000-01-07"]), range(["2000-01-07", "2000-01-10"])) | ""
        // INPUT:
        // |------------|
        //      |--|
        // EXPECTED:
        // |----|--|----|
        List.of(range(["2000-01-01", "2000-01-10"]), range(["2000-01-04", "2000-01-07"])) | Set.of(range(["2000-01-01", "2000-01-04"]), range(["2000-01-04", "2000-01-07"]), range(["2000-01-07", "2000-01-10"])) | ""
        // INPUT:
        // |----|
        //         |----|
        // EXPECTED:
        // |----|  |----|
        List.of(range(["2000-01-01", "2000-01-04"]), range(["2000-01-07", "2000-01-10"])) | Set.of(range(["2000-01-01", "2000-01-04"]), range(["2000-01-07", "2000-01-10"]))                                      | ""
        // INPUT:
        // |----|
        //      |----|
        // EXPECTED:
        // |----|----|
        List.of(range(["2000-01-01", "2000-01-04"]), range(["2000-01-04", "2000-01-07"])) | Set.of(range(["2000-01-01", "2000-01-04"]), range(["2000-01-04", "2000-01-07"]))                                      | ""
        List.of(range(["2022-04-04", "2022-04-06"]), range(["2022-04-05", "2022-04-07"])) | Set.of(range1(["2022-04-04"]), range1(["2022-04-05",]), range1(["2022-04-06",]))                                      | "overlap"

    }

    def "groupSubsequentDays"() {
        //given:

        when:
        final Set<DateRange> groupSubsequentDays = DateRange.groupSubsequentDays(days)

        then:
        Assertions.assertThat(groupSubsequentDays).containsExactlyInAnyOrderElementsOf(expectedDateRanges)

        where:
        days                                                                                                   | expectedDateRanges
        Collections.<LocalDate> emptyList()                                                                    | Collections.<DateRange> emptySet()
        List.of(day("2000-01-01"))                                                                             | Set.of(range1(["2000-01-01",]))
        List.of(day("2000-01-01"), day("2000-01-02"))                                                          | Set.of(range(["2000-01-01", "2000-01-03"]))
        List.of(day("2000-01-01"), day("2000-01-03"))                                                          | Set.of(range1(["2000-01-01",]), range1(["2000-01-03",]))
        List.of(day("2000-01-01"), day("2000-01-03"), day("2000-01-05"))                                       | Set.of(range1(["2000-01-01",]), range1(["2000-01-03",]), range1(["2000-01-05",]))
        List.of(day("2000-01-01"), day("2000-01-02"), day("2000-01-03"))                                       | Set.of(range(["2000-01-01", "2000-01-04"]))
        List.of(day("2000-01-01"), day("2000-01-02"), day("2000-01-04"), day("2000-01-05"))                    | Set.of(range(["2000-01-01", "2000-01-03"]), range(["2000-01-04", "2000-01-06"]))
        List.of(day("2000-01-01"), day("2000-01-02"), day("2000-01-04"), day("2000-01-06"), day("2000-01-07")) | Set.of(range(["2000-01-01", "2000-01-03"]), range1(["2000-01-04",]), range(["2000-01-06", "2000-01-08"]))

    }

    def "merge"() {
        //given:

        when:
        final Set<DateRange> mergedDateRanges = DateRange.merge(dateRanges)

        then:
        Assertions.assertThat(mergedDateRanges).containsExactlyInAnyOrderElementsOf(expectedDateRanges)

        where:
        dateRanges                                                                        | expectedDateRanges                                                               | comment
        Collections.<DateRange> emptyList()                                               | Collections.<DateRange> emptySet()                                               | ""
        List.of(range1(["2000-01-01",]))                                                  | Set.of(range1(["2000-01-01",]))                                                  | ""
        List.of(range(["2000-01-01", "2000-01-03"]))                                      | Set.of(range(["2000-01-01", "2000-01-03"]))                                      | ""
        List.of(range1(["2000-01-01",]), range1(["2000-01-02",]))                         | Set.of(range(["2000-01-01", "2000-01-03"]))                                      | ""
        List.of(range1(["2000-01-01",]), range(["2000-01-02", "2000-01-03"]))             | Set.of(range(["2000-01-01", "2000-01-03"]))                                      | ""
        List.of(range(["2000-01-01", "2000-01-02"]), range1(["2000-01-02",]))             | Set.of(range(["2000-01-01", "2000-01-03"]))                                      | ""
        List.of(range(["2000-01-01", "2000-01-02"]), range(["2000-01-02", "2000-01-03"])) | Set.of(range(["2000-01-01", "2000-01-03"]))                                      | ""
        List.of(range(["2000-01-01", "2000-01-03"]), range(["2000-01-03", "2000-01-07"])) | Set.of(range(["2000-01-01", "2000-01-07"]))                                      | ""
        List.of(range(["2000-01-01", "2000-01-03"]), range(["2000-01-04", "2000-01-07"])) | Set.of(range(["2000-01-01", "2000-01-03"]), range(["2000-01-04", "2000-01-07"])) | ""
        List.of(range(["2000-01-01", "2000-01-04"]), range(["2000-01-03", "2000-01-07"])) | Set.of(range(["2000-01-01", "2000-01-07"]))                                      | "overlap"
        List.of(range(["2000-01-01", "2000-01-04"]), range1(["2000-01-01",]))             | Set.of(range(["2000-01-01", "2000-01-04"]))                                      | "overlap"
        List.of(range(["2000-01-01", "2000-01-04"]), range1(["2000-01-03",]))             | Set.of(range(["2000-01-01", "2000-01-04"]))                                      | "overlap"
        List.of(range(["2000-01-01", "2000-01-04"]), range1(["2000-01-04",]))             | Set.of(range(["2000-01-01", "2000-01-05"]))                                      | ""

    }


}