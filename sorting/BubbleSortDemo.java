package sorting;

/**
 * Command-line demonstration of {@link BubbleSort}, reproducing the output of
 * the original single-file program.
 */
public final class BubbleSortDemo {

    private BubbleSortDemo() {
        // Entry-point holder; not instantiable.
    }

    public static void main(String[] args) {
        int[] data = { 64, 34, 25, 12, 22, 11, 90 };
        BubbleSort.sort(data);
        System.out.println("Sorted array: ");
        System.out.println(IntArrayFormatter.format(data));
    }
}
