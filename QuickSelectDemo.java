import java.util.Arrays;

/**
 * Small command-line demonstration of {@link QuickSelect}.
 *
 * <p>Usage:
 * <pre>
 *   java QuickSelectDemo            # uses a built-in sample array, k = 3
 *   java QuickSelectDemo 3 10 4 5   # k = 3 over the array [10, 4, 5]
 * </pre>
 * The first argument is the one-based rank {@code k}; the remaining arguments
 * are the array values.
 */
public final class QuickSelectDemo {

    private QuickSelectDemo() {
    }

    public static void main(String[] args) {
        int k;
        int[] array;

        if (args.length == 0) {
            array = new int[] { 10, 4, 5, 8, 6, 11, 26 };
            k = 3;
        } else {
            try {
                k = Integer.parseInt(args[0]);
                array = new int[args.length - 1];
                for (int i = 1; i < args.length; i++) {
                    array[i - 1] = Integer.parseInt(args[i]);
                }
            } catch (NumberFormatException e) {
                System.err.println("All arguments must be integers: " + e.getMessage());
                return;
            }
        }

        try {
            int result = QuickSelect.kthSmallest(array, k);
            System.out.println("Array: " + Arrays.toString(array));
            System.out.println(k + "-th smallest element: " + result);
        } catch (IllegalArgumentException e) {
            System.err.println("Cannot compute result: " + e.getMessage());
        }
    }
}
