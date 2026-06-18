import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

/**
 * Utility methods for computing prefix sums.
 */
public final class PrefixSumArray {

    private PrefixSumArray() {
        // Utility class.
    }

    /**
     * Computes the running total after each element in the input array.
     *
     * @param values the source values
     * @return a list of prefix sums with one entry per input value
     */
    public static List<Integer> prefixSums(int[] values) {
        int[] prefixSumArray = prefixSumsAsArray(values);

        List<Integer> prefixSums = new ArrayList<>(prefixSumArray.length);
        for (int value : prefixSumArray) {
            prefixSums.add(value);
        }

        return prefixSums;
    }

    /**
     * Computes the running total after each element in the input array.
     *
     * @param values the source values
     * @return the prefix sums as a primitive array
     */
    public static int[] prefixSumsAsArray(int[] values) {
        long[] prefixSums = prefixSumsAsLongArray(values);
        int[] boundedPrefixSums = new int[prefixSums.length];
        for (int i = 0; i < prefixSums.length; i++) {
            boundedPrefixSums[i] = Math.toIntExact(prefixSums[i]);
        }

        return boundedPrefixSums;
    }

    /**
     * Computes the running total after each element in the input array.
     *
     * @param values the source values
     * @return the prefix sums as a primitive long array
     */
    public static long[] prefixSumsAsLongArray(int[] values) {
        if (values == null) {
            throw new IllegalArgumentException("values must not be null");
        }

        long[] prefixSums = new long[values.length];
        long runningTotal = 0L;
        for (int i = 0; i < values.length; i++) {
            runningTotal += values[i];
            prefixSums[i] = runningTotal;
        }

        return prefixSums;
    }

    private static String joinPrefixSums(List<Integer> prefixSums) {
        StringJoiner joiner = new StringJoiner(" ");
        for (int value : prefixSums) {
            joiner.add(Integer.toString(value));
        }
        return joiner.toString();
    }

    public static void main(String[] args) {
        int[] values = {10, 20, 10, 5, 15};
        List<Integer> prefixSums = prefixSums(values);
        System.out.println(joinPrefixSums(prefixSums));
    }
}
