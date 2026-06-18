import java.util.List;
import java.util.StringJoiner;

public final class PrefixSumArrayDemo {

    private PrefixSumArrayDemo() {
        // Utility class.
    }

    public static void main(String[] args) {
        int[] values = {10, 20, 10, 5, 15};
        List<Integer> prefixSums = PrefixSumArray.prefixSums(values);
        System.out.println(join(prefixSums));
    }

    private static String join(List<Integer> values) {
        StringJoiner joiner = new StringJoiner(" ");
        for (int value : values) {
            joiner.add(Integer.toString(value));
        }
        return joiner.toString();
    }
}
