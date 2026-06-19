package coinchange;

import java.util.Arrays;

public record CoinDenominations(int[] values) {

    public CoinDenominations {
        validate(values);
        values = Arrays.stream(values)
            .sorted()
            .distinct()
            .toArray();
    }

    @Override
    public int[] values() {
        return Arrays.copyOf(values, values.length);
    }

    int[] rawValues() {
        return values;
    }

    int size() {
        return values.length;
    }

    private static void validate(int[] values) {
        if (values == null) {
            throw new IllegalArgumentException("coins must not be null");
        }

        for (int value : values) {
            if (value <= 0) {
                throw new IllegalArgumentException("coin values must be positive");
            }
        }
    }
}
