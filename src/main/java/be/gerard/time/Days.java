package be.gerard.time;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Set;

import static org.apache.commons.lang3.Validate.notEmpty;

public record Days(
        Set<LocalDate> dates
) {

    public Days(
            final Set<LocalDate> dates
    ) {
        notEmpty(dates);

        this.dates = Set.copyOf(dates);
    }

    static Days ofDays(
            final Set<LocalDate> days
    ) {
        return new Days(days);
    }

    static Days ofDays(
            final Collection<LocalDate> days
    ) {
        final Set<LocalDate> dates = Set.copyOf(days);
        return new Days(dates);
    }

    DateRanges asRanges() {
        return DateRanges.ofDays(dates);
    }

}
