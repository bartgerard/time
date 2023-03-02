package be.gerard.time

import spock.lang.Specification
import spock.lang.Title

import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.ChronoUnit

import static be.gerard.time.DateRangeTestUtils.*
import static org.assertj.core.api.Assertions.assertThat

@Title("DateRange")
class DateRangeSpecification extends Specification {

    def "is one day / is finite"() {

        when:
        final boolean isOneDay = dateRange.isOneDay()
        final boolean isFinite = dateRange.isFinite()

        then:
        assertThat(isOneDay).isEqualTo(expectedOneDayness)
        assertThat(isFinite).isEqualTo(expectedFiniteness)

        where:
        dateRange                           | expectedOneDayness | expectedFiniteness
        range1(["2000-01-01",])             | true               | true
        range(["2000-01-01", "2000-01-03"]) | false              | true
        range(["2000-01-01"])               | false              | false

    }

    def "length"() {

        when:
        final long length = dateRange.length()

        then:
        assertThat(length).isEqualTo(expectedLength)

        where:
        dateRange                           | expectedLength
        range1(["2000-01-01",])             | 1
        range(["2000-01-01", "2000-01-03"]) | 3
        range(["2000-01-01", "2000-01-04"]) | 4
        range(["2000-01-01"])               | Long.MAX_VALUE

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

    def "contains range"() {

        when:
        final boolean intersects = dateRange1.containsRange(dateRange2)

        then:
        assertThat(intersects).isEqualTo(isContained)

        where:
        dateRange1                          | dateRange2                          | isContained
        range(["2000-01-02",])              | range(["2000-01-01",])              | false
        range(["2000-01-02",])              | range(["2000-01-02",])              | true
        range(["2000-01-02",])              | range(["2000-01-03",])              | true

        range1(["2000-01-02",])             | range1(["2000-01-01",])             | false
        range1(["2000-01-02",])             | range1(["2000-01-02",])             | true
        range1(["2000-01-02",])             | range1(["2000-01-03",])             | false

        range(["2000-01-03", "2000-01-06"]) | range(["2000-01-01", "2000-01-02"]) | false
        range(["2000-01-03", "2000-01-06"]) | range(["2000-01-02", "2000-01-04"]) | false
        range(["2000-01-03", "2000-01-06"]) | range(["2000-01-03", "2000-01-06"]) | true
        range(["2000-01-03", "2000-01-06"]) | range(["2000-01-04", "2000-01-05"]) | true
        range(["2000-01-03", "2000-01-06"]) | range(["2000-01-05", "2000-01-06"]) | true
        range(["2000-01-03", "2000-01-06"]) | range(["2000-01-05", "2000-01-07"]) | false
        range(["2000-01-03", "2000-01-06"]) | range(["2000-01-07", "2000-01-08"]) | false

        range(["2000-01-03", "2000-01-05"]) | range(["2000-01-01", "2000-01-10"]) | false
        range(["2000-01-01", "2000-01-05"]) | range(["2000-01-03", "2000-01-10"]) | false
        range(["2000-01-03", "2000-01-10"]) | range(["2000-01-01", "2000-01-05"]) | false

        range(["2000-01-03", "2000-01-06"]) | range1(["2000-01-02",])             | false
        range(["2000-01-03", "2000-01-06"]) | range1(["2000-01-03",])             | true
        range(["2000-01-03", "2000-01-06"]) | range1(["2000-01-04",])             | true
        range(["2000-01-03", "2000-01-06"]) | range1(["2000-01-05",])             | true
        range(["2000-01-03", "2000-01-06"]) | range1(["2000-01-06",])             | true
        range(["2000-01-03", "2000-01-06"]) | range1(["2000-01-07",])             | false

        range(["2000-01-03",])              | range(["2000-01-01", "2000-01-02"]) | false
        range(["2000-01-03",])              | range(["2000-01-01", "2000-12-31"]) | false
        range(["2000-01-03",])              | range(["2000-01-03", "2000-01-06"]) | true
        range(["2000-01-03",])              | range(["2000-01-04", "2000-01-07"]) | true

        range(["2000-01-03",])              | range1(["2000-01-02",])             | false
        range(["2000-01-03",])              | range1(["2000-01-03",])             | true
        range(["2000-01-03",])              | range1(["2000-01-04",])             | true

    }

    def "is intersecting with"() {

        when:
        final boolean intersects = dateRange1.isIntersectingWith(dateRange2)

        then:
        assertThat(intersects).isEqualTo(isIntersecting)

        where:
        dateRange1                          | dateRange2                          | isIntersecting
        range(["2000-01-01",])              | range(["2000-01-01",])              | true
        range(["2000-01-01",])              | range(["2000-01-02",])              | true

        range1(["2000-01-01",])             | range1(["2000-01-01",])             | true
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

    def "intersect"() {

        when:
        final Optional<DateRange> intersection = dateRange1.intersect(dateRange2)

        then:
        assertThat(intersection).isEqualTo(expectedIntersection)

        where:
        dateRange1                          | dateRange2                          | expectedIntersection
        range1(["2000-01-01",])             | range1(["2000-01-02",])             | Optional.empty()
        range1(["2000-01-02",])             | range1(["2000-01-01",])             | Optional.empty()
        range1(["2000-01-01",])             | range1(["2000-01-01",])             | Optional.of(range1(["2000-01-01",]))

        range(["2000-01-01", "2000-01-05"]) | range(["2000-01-05", "2000-01-08"]) | Optional.of(range1(["2000-01-05",]))
        range(["2000-01-05", "2000-01-08"]) | range(["2000-01-01", "2000-01-05"]) | Optional.of(range1(["2000-01-05",]))
        range(["2000-01-01", "2000-01-05"]) | range(["2000-01-04", "2000-01-08"]) | Optional.of(range(["2000-01-04", "2000-01-05"]))
        range(["2000-01-04", "2000-01-08"]) | range(["2000-01-01", "2000-01-05"]) | Optional.of(range(["2000-01-04", "2000-01-05"]))

        range1(["2000-01-01",])             | range(["2000-01-05"])               | Optional.empty()
        range(["2000-01-05"])               | range1(["2000-01-01",])             | Optional.empty()
        range1(["2000-01-10",])             | range(["2000-01-05"])               | Optional.of(range1(["2000-01-10",]))
        range(["2000-01-05"])               | range1(["2000-01-10",])             | Optional.of(range1(["2000-01-10",]))

        range(["2000-01-01", "2000-01-05"]) | range(["2000-01-05"])               | Optional.of(range1(["2000-01-05",]))
        range(["2000-01-05"])               | range(["2000-01-01", "2000-01-05"]) | Optional.of(range1(["2000-01-05",]))
        range(["2000-01-01", "2000-01-05"]) | range(["2000-01-04"])               | Optional.of(range(["2000-01-04", "2000-01-05"]))
        range(["2000-01-04"])               | range(["2000-01-01", "2000-01-05"]) | Optional.of(range(["2000-01-04", "2000-01-05"]))

        range(["2000-01-01"])               | range(["2000-01-04"])               | Optional.of(range(["2000-01-04"]))
        range(["2000-01-04"])               | range(["2000-01-01"])               | Optional.of(range(["2000-01-04"]))

    }

    def "as days"() {

        when:
        final List<LocalDate> days = dateRange.asDays()

        then:
        assertThat(days).containsExactlyElementsOf(expectedDays)

        where:
        dateRange                           | expectedDays
        range1(["2000-01-01",])             | [day("2000-01-01")]
        range(["2000-01-01", "2000-01-05"]) | [day("2000-01-01"), day("2000-01-02"), day("2000-01-03"), day("2000-01-04"), day("2000-01-05")]

    }

    def "as months"() {

        when:
        final List<YearMonth> months = dateRange.asMonths()

        then:
        assertThat(months).containsExactlyElementsOf(expectedMonths)

        where:
        dateRange                           | expectedMonths
        range1(["2000-01-01",])             | [month("2000-01")]
        range(["2000-01-01", "2000-01-31"]) | [month("2000-01")]
        range(["2000-01-15", "2000-01-31"]) | [month("2000-01")]
        range(["2000-01-01", "2000-02-01"]) | [month("2000-01"), month("2000-02")]
        range(["2000-01-15", "2000-02-01"]) | [month("2000-01"), month("2000-02")]
        range(["2000-01-01", "2000-05-31"]) | [month("2000-01"), month("2000-02"), month("2000-03"), month("2000-04"), month("2000-05")]

    }

    def "as text"() {

        when:
        final String displayString = dateRange.asText()

        then:
        assertThat(displayString).isEqualTo(expectedDisplayString)

        where:
        dateRange                           | expectedDisplayString
        range1(["2000-01-01",])             | "[2000-01-01]"
        range(["2000-01-01", "2000-01-03"]) | "[2000-01-01,2000-01-03]"
        range(["2000-01-01",])              | "[2000-01-01,["

    }

    def "split by temporal unit"() {

        when:
        final List<DateRange> splitRanges = dateRange.splitByTemporalUnit(temporalUnit)

        then:
        assertThat(splitRanges).isEqualTo(expectedSplit)

        where:
        dateRange                           | temporalUnit      | expectedSplit                                                              | comment
        range1(["2000-01-01",])             | ChronoUnit.MONTHS | [range1(["2000-01-01",])]                                                  | ""
        range1(["2000-01-01",])             | ChronoUnit.YEARS  | [range1(["2000-01-01",])]                                                  | ""

        range(["2000-12-01", "2000-12-31"]) | ChronoUnit.MONTHS | [range(["2000-12-01", "2000-12-31"])]                                      | ""
        range(["2000-11-01", "2000-12-31"]) | ChronoUnit.MONTHS | [range(["2000-11-01", "2000-11-30"]), range(["2000-12-01", "2000-12-31"])] | ""
        range(["2000-12-15", "2001-01-15"]) | ChronoUnit.MONTHS | [range(["2000-12-15", "2000-12-31"]), range(["2001-01-01", "2001-01-15"])] | ""

        range(["2000-01-01", "2000-01-03"]) | ChronoUnit.YEARS  | [range(["2000-01-01", "2000-01-03"])]                                      | ""
        range(["2000-01-01", "2001-01-03"]) | ChronoUnit.YEARS  | [range(["2000-01-01", "2000-12-31"]), range(["2001-01-01", "2001-01-03"])] | ""
        range(["2000-01-01", "2001-01-01"]) | ChronoUnit.YEARS  | [range(["2000-01-01", "2000-12-31"]), range1(["2001-01-01",])]             | ""
        range(["2000-12-01", "2001-01-01"]) | ChronoUnit.YEARS  | [range(["2000-12-01", "2000-12-31"]), range1(["2001-01-01",])]             | ""
        range(["2000-12-31", "2001-01-01"]) | ChronoUnit.YEARS  | [range1(["2000-12-31",]), range1(["2001-01-01",])]                         | ""

    }

    def "split by day"() {

        when:
        final Set<DateRange> splitRanges = range.splitByDay(day(day))

        then:
        assertThat(splitRanges).containsExactlyInAnyOrderElementsOf(expectedSplit)

        where:
        range                               | day          | expectedSplit                                                  | comment
        range1(["2000-01-02",])             | "2000-01-01" | [range1(["2000-01-02",])]                                      | ""
        range1(["2000-01-02",])             | "2000-01-02" | [range1(["2000-01-02",])]                                      | ""
        range1(["2000-01-02",])             | "2000-01-03" | [range1(["2000-01-02",]),]                                     | ""

        range(["2000-01-02", "2000-01-04"]) | "2000-01-01" | [range(["2000-01-02", "2000-01-04"])]                          | ""
        range(["2000-01-02", "2000-01-04"]) | "2000-01-02" | [range(["2000-01-02", "2000-01-04"])]                          | ""
        range(["2000-01-02", "2000-01-04"]) | "2000-01-03" | [range1(["2000-01-02",]), range(["2000-01-03", "2000-01-04"])] | ""
        range(["2000-01-02", "2000-01-04"]) | "2000-01-04" | [range(["2000-01-02", "2000-01-03"]), range1(["2000-01-04",])] | ""
        range(["2000-01-02", "2000-01-04"]) | "2000-01-05" | [range(["2000-01-02", "2000-01-04"]),]                         | ""

        range(["2000-01-02",])              | "2000-01-01" | [range(["2000-01-02",])]                                       | ""
        range(["2000-01-02",])              | "2000-01-02" | [range(["2000-01-02",])]                                       | ""
        range(["2000-01-02",])              | "2000-01-03" | [range1(["2000-01-02",]), range(["2000-01-03",])]              | ""

    }

    def "split by range"() {

        when:
        final Set<DateRange> splitRanges = range.splitByRange(otherRange)

        then:
        assertThat(splitRanges).containsExactlyInAnyOrderElementsOf(expectedSplit)

        where:
        range                               | otherRange                          | expectedSplit                                                                                      | comment
        range1(["2000-01-02",])             | range1(["2000-01-01",])             | [range1(["2000-01-02",])]                                                                          | ""
        range1(["2000-01-02",])             | range1(["2000-01-02",])             | [range1(["2000-01-02",])]                                                                          | ""
        range1(["2000-01-02",])             | range1(["2000-01-03",])             | [range1(["2000-01-02",]),]                                                                         | ""

        range(["2000-01-02", "2000-01-04"]) | range1(["2000-01-01",])             | [range(["2000-01-02", "2000-01-04"])]                                                              | ""
        range(["2000-01-02", "2000-01-04"]) | range1(["2000-01-02",])             | [range1(["2000-01-02",]), range(["2000-01-03", "2000-01-04"])]                                     | ""
        range(["2000-01-02", "2000-01-04"]) | range1(["2000-01-03",])             | [range1(["2000-01-02",]), range1(["2000-01-03",]), range1(["2000-01-04",])]                        | ""
        range(["2000-01-02", "2000-01-04"]) | range1(["2000-01-04",])             | [range(["2000-01-02", "2000-01-03"]), range1(["2000-01-04",])]                                     | ""
        range(["2000-01-02", "2000-01-04"]) | range1(["2000-01-05",])             | [range(["2000-01-02", "2000-01-04"]),]                                                             | ""

        range(["2000-01-02",])              | range1(["2000-01-01",])             | [range(["2000-01-02",])]                                                                           | ""
        range(["2000-01-02",])              | range1(["2000-01-02",])             | [range1(["2000-01-02",]), range(["2000-01-03",])]                                                  | ""
        range(["2000-01-02",])              | range1(["2000-01-03",])             | [range1(["2000-01-02",]), range1(["2000-01-03",]), range(["2000-01-04",])]                         | ""
        range(["2000-01-02",])              | range1(["2000-01-04",])             | [range(["2000-01-02", "2000-01-03"]), range1(["2000-01-04",]), range(["2000-01-05",])]             | ""

        range1(["2000-01-02",])             | range(["2000-01-01", "2000-01-03"]) | [range1(["2000-01-02",])]                                                                          | ""
        range1(["2000-01-02",])             | range(["2000-01-02", "2000-01-03"]) | [range1(["2000-01-02",])]                                                                          | ""
        range1(["2000-01-02",])             | range(["2000-01-03", "2000-01-03"]) | [range1(["2000-01-02",]),]                                                                         | ""

        range(["2000-01-03", "2000-01-06"]) | range(["2000-01-01", "2000-01-02"]) | [range(["2000-01-03", "2000-01-06"])]                                                              | ""
        range(["2000-01-03", "2000-01-06"]) | range(["2000-01-01", "2000-01-03"]) | [range1(["2000-01-03",]), range(["2000-01-04", "2000-01-06"])]                                     | ""
        range(["2000-01-03", "2000-01-06"]) | range(["2000-01-01", "2000-01-04"]) | [range(["2000-01-03", "2000-01-04"]), range(["2000-01-05", "2000-01-06"])]                         | ""
        range(["2000-01-03", "2000-01-06"]) | range(["2000-01-03", "2000-01-04"]) | [range(["2000-01-03", "2000-01-04"]), range(["2000-01-05", "2000-01-06"])]                         | ""
        range(["2000-01-03", "2000-01-06"]) | range(["2000-01-04", "2000-01-05"]) | [range1(["2000-01-03",]), range(["2000-01-04", "2000-01-05"]), range1(["2000-01-06",])]            | ""
        range(["2000-01-03", "2000-01-06"]) | range(["2000-01-05", "2000-01-06"]) | [range(["2000-01-03", "2000-01-04"]), range(["2000-01-05", "2000-01-06"])]                         | ""
        range(["2000-01-03", "2000-01-06"]) | range(["2000-01-05", "2000-01-08"]) | [range(["2000-01-03", "2000-01-04"]), range(["2000-01-05", "2000-01-06"])]                         | ""
        range(["2000-01-03", "2000-01-06"]) | range(["2000-01-06", "2000-01-08"]) | [range(["2000-01-03", "2000-01-05"]), range1(["2000-01-06",])]                                     | ""
        range(["2000-01-03", "2000-01-06"]) | range(["2000-01-07", "2000-01-08"]) | [range(["2000-01-03", "2000-01-06"])]                                                              | ""

        range(["2000-01-03",])              | range(["2000-01-01", "2000-01-02"]) | [range(["2000-01-03",])]                                                                           | ""
        range(["2000-01-03",])              | range(["2000-01-02", "2000-01-03"]) | [range1(["2000-01-03",]), range(["2000-01-04",])]                                                  | ""
        range(["2000-01-03",])              | range(["2000-01-04", "2000-01-05"]) | [range1(["2000-01-03",]), range(["2000-01-04", "2000-01-05"]), range(["2000-01-06",])]             | ""
        range(["2000-01-03",])              | range(["2000-01-05", "2000-01-06"]) | [range(["2000-01-03", "2000-01-04"]), range(["2000-01-05", "2000-01-06"]), range(["2000-01-07",])] | ""

        range1(["2000-01-02",])             | range(["2000-01-01"])               | [range1(["2000-01-02",])]                                                                          | ""
        range1(["2000-01-02",])             | range(["2000-01-02"])               | [range1(["2000-01-02",])]                                                                          | ""
        range1(["2000-01-02",])             | range(["2000-01-03"])               | [range1(["2000-01-02",]),]                                                                         | ""

        range(["2000-01-03", "2000-01-06"]) | range(["2000-01-01",])              | [range(["2000-01-03", "2000-01-06"])]                                                              | ""
        range(["2000-01-03", "2000-01-06"]) | range(["2000-01-03",])              | [range(["2000-01-03", "2000-01-06"])]                                                              | ""
        range(["2000-01-03", "2000-01-06"]) | range(["2000-01-05",])              | [range(["2000-01-03", "2000-01-04"]), range(["2000-01-05", "2000-01-06"])]                         | ""
        range(["2000-01-03", "2000-01-06"]) | range(["2000-01-06",])              | [range(["2000-01-03", "2000-01-05"]), range1(["2000-01-06",])]                                     | ""
        range(["2000-01-03", "2000-01-06"]) | range(["2000-01-07",])              | [range(["2000-01-03", "2000-01-06"])]                                                              | ""

        range(["2000-01-02",])              | range(["2000-01-01"])               | [range(["2000-01-02",])]                                                                           | ""
        range(["2000-01-02",])              | range(["2000-01-02"])               | [range(["2000-01-02",])]                                                                           | ""
        range(["2000-01-02",])              | range(["2000-01-03"])               | [range1(["2000-01-02",]), range(["2000-01-03",]),]                                                 | ""

    }

    def "subtract1"() {

        when:
        final List<DateRange> difference = minuend.subtract(subtrahend)

        then:
        assertThat(difference).isEqualTo(expectedDifference)

        where:
        minuend                             | subtrahend                          | expectedDifference                                                         | comment
        range1(["2000-01-01",])             | range1(["2000-01-01",])             | []                                                                         | ""
        range1(["2000-01-01",])             | range1(["2000-01-02",])             | [range1(["2000-01-01",])]                                                  | ""
        range1(["2000-01-01",])             | range(["1999-01-01",])              | []                                                                         | ""
        range1(["2000-01-01",])             | range(["2000-01-02",])              | [range1(["2000-01-01",])]                                                  | ""

        range(["2000-12-01", "2000-12-31"]) | range1(["2000-01-02",])             | [range(["2000-12-01", "2000-12-31"])]                                      | ""
        range(["2000-01-01", "2000-12-31"]) | range1(["2000-01-02",])             | [range1(["2000-01-01",]), range(["2000-01-03", "2000-12-31"])]             | ""
        range(["2000-01-01",])              | range1(["2000-01-02",])             | [range1(["2000-01-01",]), range(["2000-01-03",])]                          | ""

        range(["2000-01-01", "2000-01-06"]) | range(["2000-01-03", "2000-01-04"]) | [range(["2000-01-01", "2000-01-02"]), range(["2000-01-05", "2000-01-06"])] | ""
        range(["2000-01-01", "2000-01-06"]) | range(["1999-01-01", "2000-01-04"]) | [range(["2000-01-05", "2000-01-06"])]                                      | ""
        range(["2000-01-01", "2000-01-06"]) | range(["2000-01-03", "2001-01-01"]) | [range(["2000-01-01", "2000-01-02"])]                                      | ""
        range(["2000-01-01", "2000-01-06"]) | range(["1999-01-01", "2001-01-01"]) | []                                                                         | ""

        range(["2000-01-02",])              | range(["2000-01-01",])              | []                                                                         | ""
        range(["2000-01-02",])              | range(["2000-01-02",])              | []                                                                         | ""
        range(["2000-01-02",])              | range(["2000-01-03",])              | [range1(["2000-01-02",])]                                                  | ""

        range1(["2000-01-02",])             | range(["2000-01-01",])              | []                                                                         | ""
        range1(["2000-01-02",])             | range(["2000-01-02",])              | []                                                                         | ""
        range1(["2000-01-02",])             | range(["2000-01-03",])              | [range1(["2000-01-02",])]                                                  | ""

        range(["2000-01-02", "2000-01-06"]) | range(["2000-01-01",])              | []                                                                         | ""
        range(["2000-01-02", "2000-01-06"]) | range(["2000-01-02",])              | []                                                                         | ""
        range(["2000-01-02", "2000-01-06"]) | range(["2000-01-03",])              | [range1(["2000-01-02",])]                                                  | ""
        range(["2000-01-02", "2000-01-06"]) | range(["2000-01-04",])              | [range(["2000-01-02", "2000-01-03"])]                                      | ""

    }

    def "subtract"() {

        when:
        final List<DateRange> difference = minuend.subtract(subtrahends)

        then:
        assertThat(difference).isEqualTo(expectedDifference)

        where:
        minuend                             | subtrahends                                                                | expectedDifference                                                                                              | comment
        range1(["2000-01-01",])             | [range1(["2000-01-01",])]                                                  | []                                                                                                              | ""
        range1(["2000-01-01",])             | [range1(["2000-01-02",])]                                                  | [range1(["2000-01-01",])]                                                                                       | ""
        range1(["2000-01-01",])             | [range(["1999-01-01",])]                                                   | []                                                                                                              | ""
        range1(["2000-01-01",])             | [range(["2000-01-02",])]                                                   | [range1(["2000-01-01",])]                                                                                       | ""

        range(["2000-12-01", "2000-12-31"]) | [range1(["2000-01-02",])]                                                  | [range(["2000-12-01", "2000-12-31"])]                                                                           | ""
        range(["2000-01-01", "2000-12-31"]) | [range1(["2000-01-02",])]                                                  | [range1(["2000-01-01",]), range(["2000-01-03", "2000-12-31"])]                                                  | ""
        range(["2000-01-01",])              | [range1(["2000-01-02",])]                                                  | [range1(["2000-01-01",]), range(["2000-01-03",])]                                                               | ""

        range(["2000-01-01", "2000-01-06"]) | [range(["2000-01-03", "2000-01-04"])]                                      | [range(["2000-01-01", "2000-01-02"]), range(["2000-01-05", "2000-01-06"])]                                      | ""
        range(["2000-01-01", "2000-01-06"]) | [range(["1999-01-01", "2000-01-04"])]                                      | [range(["2000-01-05", "2000-01-06"])]                                                                           | ""
        range(["2000-01-01", "2000-01-06"]) | [range(["2000-01-03", "2001-01-01"])]                                      | [range(["2000-01-01", "2000-01-02"])]                                                                           | ""
        range(["2000-01-01", "2000-01-06"]) | [range(["1999-01-01", "2001-01-01"])]                                      | []                                                                                                              | ""

        range(["2000-01-01", "2000-01-10"]) | [range(["2000-01-03", "2000-01-04"]), range(["2000-01-07", "2000-01-08"])] | [range(["2000-01-01", "2000-01-02"]), range(["2000-01-05", "2000-01-06"]), range(["2000-01-09", "2000-01-10"])] | ""
        range(["2000-01-01", "2000-01-10"]) | [range(["2000-01-03", "2000-01-05"]), range(["2000-01-06", "2000-01-08"])] | [range(["2000-01-01", "2000-01-02"]), range(["2000-01-09", "2000-01-10"])]                                      | ""
        range(["2000-01-01", "2000-01-10"]) | [range(["2000-01-03", "2000-01-06"]), range(["2000-01-06", "2000-01-08"])] | [range(["2000-01-01", "2000-01-02"]), range(["2000-01-09", "2000-01-10"])]                                      | ""
        range(["2000-01-01", "2000-01-10"]) | [range(["2000-01-03", "2000-01-07"]), range(["2000-01-06", "2000-01-08"])] | [range(["2000-01-01", "2000-01-02"]), range(["2000-01-09", "2000-01-10"])]                                      | ""

        range(["2000-01-01", "2000-01-10"]) | [range(["2000-01-01", "2000-01-04"]), range(["2000-01-07", "2000-01-08"])] | [range(["2000-01-05", "2000-01-06"]), range(["2000-01-09", "2000-01-10"])]                                      | ""
        range(["2000-01-01", "2000-01-10"]) | [range(["1999-01-01", "2000-01-04"]), range(["2000-01-07", "2000-01-08"])] | [range(["2000-01-05", "2000-01-06"]), range(["2000-01-09", "2000-01-10"])]                                      | ""
        range(["2000-01-01", "2000-01-10"]) | [range(["2000-01-03", "2000-01-04"]), range(["2000-01-07", "2001-01-01"])] | [range(["2000-01-01", "2000-01-02"]), range(["2000-01-05", "2000-01-06"])]                                      | ""
        range(["2000-01-01", "2000-01-10"]) | [range(["1999-01-01", "2000-01-04"]), range(["2000-01-07", "2001-01-01"])] | [range(["2000-01-05", "2000-01-06"])]                                                                           | ""
        range(["2000-01-01", "2000-01-10"]) | [range(["1999-01-01", "2000-01-05"]), range(["2000-01-06", "2001-01-01"])] | []                                                                                                              | ""

    }


}