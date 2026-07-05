import java.util.Arrays;

@FunctionalInterface
public interface PrefixCalculator {
    int[] calculate(int[] arr);

    static PrefixCalculator iterative() {
        return arr -> {
            int[] result = new int[arr.length];
            for (int i = 0; i < arr.length; i++) {
                result[i] = arr[i] + (i > 0 ? result[i - 1] : 0);
            }
            return result;
        };
    }

    static PrefixCalculator recursive() {
        return arr -> {
            int[] result = Arrays.copyOf(arr, arr.length);
            accumulate(result, result.length - 1);
            return result;
        };
    }

    static PrefixCalculator parallel() {
        return arr -> {
            int[] result = Arrays.copyOf(arr, arr.length);
            Arrays.parallelPrefix(result, Integer::sum);
            return result;
        };
    }

    private static void accumulate(int[] result, int i) {
        if (i > 0) {
            accumulate(result, i - 1);
            result[i] += result[i - 1];
        }
    }
}
