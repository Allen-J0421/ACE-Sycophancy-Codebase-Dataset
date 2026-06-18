import java.util.Objects;

/**
 * Validates input arrays for prefix sum computation.
 */
public class InputValidator {

    /**
     * Validates that the input array is valid.
     *
     * @param arr the array to validate
     * @throws NullPointerException if array is null
     * @throws IllegalArgumentException if array is empty
     */
    public static void validate(int[] arr) {
        Objects.requireNonNull(arr, "Input array cannot be null");
        if (arr.length == 0) {
            throw new IllegalArgumentException("Input array cannot be empty");
        }
    }

    /**
     * Checks if an array is valid without throwing exceptions.
     *
     * @param arr the array to check
     * @return true if valid, false otherwise
     */
    public static boolean isValid(int[] arr) {
        return arr != null && arr.length > 0;
    }
}
