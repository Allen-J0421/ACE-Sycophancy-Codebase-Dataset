import java.util.Arrays;

/**
 * Small command-line demo of {@link QuickSort}. Kept separate from the library
 * so the sorting logic carries no I/O concerns and stays reusable.
 */
public final class QuickSortDemo {

    private QuickSortDemo() {
        // Entry-point holder — not instantiable.
    }

    public static void main(String[] args) {
        int[] arr = {10, 7, 8, 9, 1, 5};
        QuickSort.sort(arr);

        // Preserve the original output format: space-separated values.
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            if (i > 0) {
                out.append(' ');
            }
            out.append(arr[i]);
        }
        System.out.println(out);

        // Bonus: the same algorithm sorting objects via the generic API.
        String[] words = {"pear", "apple", "fig", "banana"};
        QuickSort.sort(words);
        System.out.println(Arrays.toString(words));
    }
}
