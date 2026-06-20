import java.util.Arrays;

public final class HeapSortDemo {

    private HeapSortDemo() {
        // Utility class; do not instantiate.
    }

    public static void main(String[] args) {
        runCase(new int[] {9, 4, 3, 8, 10, 2, 5}, "[2, 3, 4, 5, 8, 9, 10]");
        runCase(new int[] {}, "[]");
        runCase(new int[] {1}, "[1]");
        runCase(new int[] {3, 3, 2, 1, 2}, "[1, 2, 2, 3, 3]");
    }

    private static void runCase(int[] values, String expected) {
        HeapSort.sort(values);

        String actual = Arrays.toString(values);
        if (!expected.equals(actual)) {
            throw new AssertionError("Expected " + expected + " but got " + actual);
        }

        System.out.println(actual);
    }
}
