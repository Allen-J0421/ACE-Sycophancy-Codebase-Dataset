import java.util.List;

public class PrefixSumDemo {
    public static void main(String[] args) {
        // Integer — convenience overload, iterative by default
        RangeSumQuery<Integer> intPs = PrefixSum.of(new int[]{10, 20, 10, 5, 15});
        System.out.println(intPs.toList());       // [10, 30, 40, 45, 60]
        System.out.println(intPs.rangeSum(1, 3)); // 35

        // Long
        RangeSumQuery<Long> longPs = PrefixSum.of(
                List.of(10L, 20L, 10L, 5L, 15L), Long::sum, () -> 0L, (a, b) -> a - b);
        System.out.println(longPs.toList());      // [10, 30, 40, 45, 60]

        // Double with recursive strategy
        RangeSumQuery<Double> doublePs = PrefixSum.of(
                List.of(1.5, 2.5, 3.0), Double::sum, () -> 0.0, (a, b) -> a - b,
                PrefixCalculator.recursive());
        System.out.println(doublePs.toList());    // [1.5, 4.0, 7.0]
    }
}
