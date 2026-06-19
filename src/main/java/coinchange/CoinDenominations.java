package coinchange;

import java.util.Arrays;
import java.util.List;

public record CoinDenominations(List<Integer> values) {

    public CoinDenominations(int[] values) {
        this(toValueList(values));
    }

    public CoinDenominations {
        validate(values);
        values = values.stream()
            .sorted()
            .distinct()
            .toList();
    }

    private static List<Integer> toValueList(int[] values) {
        if (values == null) {
            throw new IllegalArgumentException("coins must not be null");
        }

        return Arrays.stream(values)
            .boxed()
            .toList();
    }

    private static void validate(List<Integer> values) {
        if (values == null) {
            throw new IllegalArgumentException("coins must not be null");
        }

        for (Integer value : values) {
            if (value == null) {
                throw new IllegalArgumentException("coin values must not be null");
            }
            if (value <= 0) {
                throw new IllegalArgumentException("coin values must be positive");
            }
        }
    }
}
