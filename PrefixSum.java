import java.util.Objects;

public final class PrefixSum {

    private PrefixSum() {
    }

    public static int[] build(int[] values) {
        Objects.requireNonNull(values, "values must not be null");

        int[] prefixSums = new int[values.length];
        int runningTotal = 0;

        for (int i = 0; i < values.length; i++) {
            runningTotal += values[i];
            prefixSums[i] = runningTotal;
        }

        return prefixSums;
    }

    public static String format(int[] values) {
        Objects.requireNonNull(values, "values must not be null");

        StringBuilder output = new StringBuilder();

        for (int i = 0; i < values.length; i++) {
            if (i > 0) {
                output.append(' ');
            }
            output.append(values[i]);
        }

        return output.toString();
    }

    public static void main(String[] args) {
        int[] values = {10, 20, 10, 5, 15};
        int[] prefixSums = build(values);
        System.out.println(format(prefixSums));
    }
}
