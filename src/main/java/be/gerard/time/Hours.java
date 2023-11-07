package be.gerard.time;

import java.math.BigDecimal;

import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.notNull;

public record Hours(
        BigDecimal value
) {
    private static final BigDecimal MIN_VALUE = BigDecimal.ZERO;
    private static final BigDecimal MAX_VALUE = BigDecimal.valueOf(24);

    public Hours {
        notNull(value, "hours.value is invalid [null]");

        isTrue(MIN_VALUE.compareTo(value) <= 0, "hours.value must be >= 0.00 [value=%s]", value);
        isTrue(MAX_VALUE.compareTo(value) >= 0, "hours.value must be <= 24.00 [value=%s]", value);
    }

    public static Hours of(final BigDecimal value) {
        return new Hours(value);
    }

    public static Hours of(final int value) {
        return of(BigDecimal.valueOf(value));
    }

}
