import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BinaryOperator;

@FunctionalInterface
public interface PrefixCalculator<T> {
    List<T> calculate(List<T> input, BinaryOperator<T> op, ZeroValueProvider<T> zero);

    static <T> PrefixCalculator<T> iterative() {
        return (input, op, zero) -> {
            List<T> result = new ArrayList<>(input.size());
            for (int i = 0; i < input.size(); i++) {
                T prev = i > 0 ? result.get(i - 1) : zero.zero();
                result.add(op.apply(prev, input.get(i)));
            }
            return result;
        };
    }

    static <T> PrefixCalculator<T> recursive() {
        return (input, op, zero) -> accumulateRecursive(input, op, input.size() - 1);
    }

    static <T> PrefixCalculator<T> parallel() {
        return (input, op, zero) -> {
            @SuppressWarnings("unchecked")
            T[] arr = (T[]) input.toArray();
            Arrays.parallelPrefix(arr, op);
            return new ArrayList<>(Arrays.asList(arr));
        };
    }

    private static <T> List<T> accumulateRecursive(List<T> input, BinaryOperator<T> op, int upTo) {
        if (upTo == 0) {
            List<T> base = new ArrayList<>();
            base.add(input.get(0));
            return base;
        }
        List<T> prev = accumulateRecursive(input, op, upTo - 1);
        prev.add(op.apply(prev.get(prev.size() - 1), input.get(upTo)));
        return prev;
    }
}
