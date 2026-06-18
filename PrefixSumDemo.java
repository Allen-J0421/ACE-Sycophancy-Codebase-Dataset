import java.util.List;
import java.util.StringJoiner;

public final class PrefixSumDemo {

    private static final String VALUE_SEPARATOR = " ";

    private PrefixSumDemo() {
    }

    public static void main(String[] args) {
        printPrefixSums(sampleValues());
    }

    private static int[] sampleValues() {
        return new int[] {10, 20, 10, 5, 15};
    }

    private static void printPrefixSums(int[] values) {
        System.out.println(formatValues(PrefixSum.prefixSums(values)));
    }

    private static String formatValues(List<Integer> values) {
        StringJoiner joinedValues = new StringJoiner(VALUE_SEPARATOR);
        for (int value : values) {
            joinedValues.add(Integer.toString(value));
        }
        return joinedValues.toString();
    }
}
