package prefixsum;

import java.util.Objects;

public final class PrefixSums {

    private PrefixSums() {
    }

    public static PrefixSumArray of(int[] values) {
        int[] source = requireValues(values);
        int[] prefixSums = new int[source.length];
        fill(source, prefixSums);
        return new PrefixSumArray(prefixSums);
    }

    public static int[] build(int[] values) {
        return of(values).toArray();
    }

    public static void fill(int[] values, int[] destination) {
        int[] source = requireValues(values);
        int[] target = requireDestination(destination);
        requireMatchingLength(source, target);

        int runningTotal = 0;

        for (int i = 0; i < source.length; i++) {
            runningTotal += source[i];
            target[i] = runningTotal;
        }
    }

    private static int[] requireValues(int[] values) {
        return Objects.requireNonNull(values, "values must not be null");
    }

    private static int[] requireDestination(int[] destination) {
        return Objects.requireNonNull(destination, "destination must not be null");
    }

    private static void requireMatchingLength(int[] values, int[] destination) {
        if (destination.length != values.length) {
            throw new IllegalArgumentException("destination length must match values length");
        }
    }
}
