import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Computes prefix sum arrays efficiently.
 * The prefix sum at index i is the sum of all elements from index 0 to i.
 */
public class PrefixSum {

    /**
     * Computes the prefix sum of an array.
     *
     * @param arr the input array
     * @return a list where each element is the prefix sum up to that index
     * @throws IllegalArgumentException if the array is null or empty
     */
    public static List<Long> computePrefixSum(int[] arr) {
        Objects.requireNonNull(arr, "Input array cannot be null");
        if (arr.length == 0) {
            throw new IllegalArgumentException("Input array cannot be empty");
        }

        List<Long> result = new ArrayList<>(arr.length);
        long sum = 0;

        for (int value : arr) {
            sum += value;
            result.add(sum);
        }

        return result;
    }
}
