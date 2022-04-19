package be.gerard.time

import be.gerard.time.model.DateRange
import org.assertj.core.api.Assertions
import spock.lang.Specification
import spock.lang.Title

import java.time.LocalDate

import static be.gerard.time.DateRangeTestUtils.*

@Title("DateRange Tests")
class DateRangeSpecification extends Specification {

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

    }

    def "groupSubsequentDays"() {
        //given:

        when:
        final Set<DateRange> groupSubsequentDays = DateRange.groupSubsequentDays(days)

        then:
        Assertions.assertThat(groupSubsequentDays).containsExactlyInAnyOrderElementsOf(expectedDateRanges)

        where:
        days                                                             | expectedDateRanges
        Collections.<LocalDate> emptyList()                              | Collections.<DateRange> emptySet()
        List.of(day("2000-01-01"))                                       | Set.of(range1(["2000-01-01",]))
        List.of(day("2000-01-01"), day("2000-01-02"))                    | Set.of(range(["2000-01-01", "2000-01-03"]))
        List.of(day("2000-01-01"), day("2000-01-03"))                    | Set.of(range1(["2000-01-01",]), range1(["2000-01-03",]))
        List.of(day("2000-01-01"), day("2000-01-02"), day("2000-01-03")) | Set.of(range(["2000-01-01", "2000-01-04"]))

    }

}