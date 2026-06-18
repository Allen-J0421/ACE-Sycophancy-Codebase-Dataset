import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

public final class PrefixSum {

    private static final int[] SAMPLE_VALUES = {10, 20, 10, 5, 15};
    private static final String VALUE_SEPARATOR = " ";

    private PrefixSum() {
    }

    public static List<Integer> prefixSums(int[] values) {
        Objects.requireNonNull(values, "values must not be null");

        List<Integer> prefixSums = new ArrayList<>(values.length);
        int runningTotal = 0;

        for (int value : values) {
            runningTotal += value;
            prefixSums.add(runningTotal);
        }

        return Collections.unmodifiableList(prefixSums);
    }

    public static void main(String[] args) {
        printPrefixSums(SAMPLE_VALUES);
    }

    private static void printPrefixSums(int[] values) {
        System.out.println(formatValues(prefixSums(values)));
    }

    private static String formatValues(List<Integer> values) {
        StringJoiner joinedValues = new StringJoiner(VALUE_SEPARATOR);
        for (int value : values) {
            joinedValues.add(Integer.toString(value));
        }
        return joinedValues.toString();
    }
}
