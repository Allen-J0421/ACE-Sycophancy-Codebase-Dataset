import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BinaryOperator;

import static java.util.Objects.requireNonNull;

public class PrefixSum<T> implements RangeSumQuery<T> {
    private final List<T> prefix;
    private final BinaryOperator<T> difference;

    public static PrefixSum<Integer> of(int[] arr) {
        List<Integer> input = new ArrayList<>(arr.length);
        for (int x : arr) input.add(x);
        return of(input, Integer::sum, () -> 0, (a, b) -> a - b);
    }

    public static <T> PrefixSum<T> of(List<T> input, BinaryOperator<T> op,
                                       ZeroValueProvider<T> zero, BinaryOperator<T> difference) {
        return new PrefixSum<>(input, op, zero, difference, PrefixCalculator.iterative());
    }

    public static <T> PrefixSum<T> of(List<T> input, BinaryOperator<T> op,
                                       ZeroValueProvider<T> zero, BinaryOperator<T> difference,
                                       PrefixCalculator<T> calculator) {
        return new PrefixSum<>(input, op, zero, difference, calculator);
    }

    private PrefixSum(List<T> input, BinaryOperator<T> op, ZeroValueProvider<T> zero,
                      BinaryOperator<T> difference, PrefixCalculator<T> calculator) {
        requireNonNull(input,      "input");
        requireNonNull(op,         "op");
        requireNonNull(zero,       "zero");
        requireNonNull(difference, "difference");
        requireNonNull(calculator, "calculator");
        Validator.requireNonEmpty(input, "input");
        this.prefix     = Collections.unmodifiableList(calculator.calculate(input, op, zero));
        this.difference = difference;
    }

    @Override
    public T rangeSum(int l, int r) {
        if (l < 0 || r >= prefix.size()) throw new IndexOutOfBoundsException(
                "Range [" + l + ", " + r + "] out of bounds for length " + prefix.size());
        if (l > r) throw new IllegalArgumentException("l (" + l + ") must not exceed r (" + r + ")");
        return l > 0 ? difference.apply(prefix.get(r), prefix.get(l - 1)) : prefix.get(r);
    }

    @Override
    public List<T> toList() {
        return prefix;
    }

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
