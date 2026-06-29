import java.io.PrintStream;
import java.util.Objects;

final class BinarySearchDemo {
    private static final int[] SAMPLE_ARRAY = { 2, 3, 4, 10, 40 };
    private static final int SAMPLE_TARGET = 10;

    private BinarySearchDemo() {
    }

    static void run() {
        run(System.out);
    }

    static void run(PrintStream output) {
        Objects.requireNonNull(output, "output");

        int result = BinarySearch.binarySearch(SAMPLE_ARRAY, SAMPLE_TARGET);

        output.println(SearchResultFormatter.format(result));
    }
}
