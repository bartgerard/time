package be.gerard.time;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

class HoursTest {

    @Test
    public void invalid_hours_null() {
        assertThatNullPointerException()
                .isThrownBy(() -> Hours.of(null))
                .withMessage("hours.value is invalid [null]");
    }

    @Test
    public void invalid_hours_beyond_max() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> Hours.of(25))
                .withMessage("hours.value must be <= 24.00 [value=25]");
    }

    @Test
    public void invalid_hours_below_min() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> Hours.of(-1))
                .withMessage("hours.value must be >= 0.00 [value=-1]");
    }

    @ParameterizedTest
    @ValueSource(ints = {
            0, 1, 23, 24
    })
    public void valid(final int value) {
        assertThat(Hours.of(value).value()).isEqualTo(BigDecimal.valueOf(value));
    }

}