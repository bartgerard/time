package be.gerard.time.api;

import org.junit.jupiter.api.Test;

import java.util.Map;

public class ExperimentTest {

    @Test
    public void a() {
        final Map<String, Integer> expectedResult = Map.ofEntries(
                Map.entry("100", 4),
                Map.entry("230", 4)
        );
    }

}
