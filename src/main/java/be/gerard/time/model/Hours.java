package be.gerard.time.model;

import org.apache.commons.lang3.Validate;

import java.math.BigDecimal;

public record Hours(
        BigDecimal value
) {
    private static final BigDecimal MIN_VALUE = BigDecimal.ZERO;
    private static final BigDecimal MAX_VALUE = BigDecimal.valueOf(24);

    public Hours {
        Validate.notNull(value);
        Validate.isTrue(MIN_VALUE.compareTo(value) <= 0, "hours.value must be >= 0.00 [value=%s]", value);
        Validate.isTrue(MAX_VALUE.compareTo(value) >= 0, "hours.value must be <= 24.00 [value=%s]", value);
    }

    public static Hours of(final BigDecimal value) {
        return new Hours(value);
    }

    public static Hours of(final int value) {
        return new Hours(BigDecimal.valueOf(value));
    }

}
