package prefixsum;

import java.util.List;

public final class PrefixSumArrayDemo {

    private static final int[] SAMPLE_VALUES = {10, 20, 10, 5, 15};

    private PrefixSumArrayDemo() {
        // Utility class.
    }

    public static void main(String[] args) {
        int[] values = args.length == 0 ? SAMPLE_VALUES : PrefixSumParser.parse(args);
        List<Integer> prefixSums = PrefixSumArray.prefixSums(values);
        System.out.println(PrefixSumFormatter.join(prefixSums));
    }
}
