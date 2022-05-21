package be.gerard.time.api;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class HoursTest {

    @Test
    public void invalid_hours_null() {
        final NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> Hours.of(null)
        );

        assertThat(exception.getMessage()).isEqualTo("The validated object is null");
    }

    @Test
    public void invalid_hours_beyond_max() {
        final IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> Hours.of(25)
        );

        assertThat(exception.getMessage()).isEqualTo("hours.value must be <= 24.00 [value=25]");
    }

    @Test
    public void invalid_hours_below_min() {
        final IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> Hours.of(-1)
        );

        assertThat(exception.getMessage()).isEqualTo("hours.value must be >= 0.00 [value=-1]");
    }

    @ParameterizedTest
    @ValueSource(ints = {
            0, 1, 23, 24
    })
    public void valid(final int value) {
        Assertions.assertThat(Hours.of(value).value()).isEqualTo(BigDecimal.valueOf(value));
    }

}