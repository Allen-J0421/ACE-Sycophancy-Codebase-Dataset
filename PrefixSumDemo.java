import java.util.List;

/**
 * Demo class for PrefixSum computation.
 */
public class PrefixSumDemo {

    private static final int[] SAMPLE_ARRAY = {10, 20, 10, 5, 15};

    public static void main(String[] args) {
        List<Long> result = PrefixSum.computePrefixSum(SAMPLE_ARRAY);
        System.out.println("Prefix sum array: " + result);
    }
}
