import java.util.Objects;

public final class PrefixSum {

    private PrefixSum() {
    }

    public static int[] build(int[] values) {
        Objects.requireNonNull(values, "values must not be null");

        int[] prefixSums = new int[values.length];
        buildInto(values, prefixSums);
        return prefixSums;
    }

    public static void buildInto(int[] values, int[] destination) {
        Objects.requireNonNull(values, "values must not be null");
        Objects.requireNonNull(destination, "destination must not be null");

        if (destination.length != values.length) {
            throw new IllegalArgumentException("destination length must match values length");
        }

        int runningTotal = 0;

        for (int i = 0; i < values.length; i++) {
            runningTotal += values[i];
            destination[i] = runningTotal;
        }
    }
}
