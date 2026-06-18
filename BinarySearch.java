import java.util.Objects;

public final class BinarySearch {
    public static final int NOT_FOUND = -1;

    private BinarySearch() {
    }

    public static int binarySearch(int[] values, int target) {
        return indexOf(values, target);
    }

    public static int indexOf(int[] values, int target) {
        Objects.requireNonNull(values, "values");

        int left = 0;
        int right = values.length - 1;

        while (left <= right) {
            int middle = middleIndex(left, right);
            int candidate = values[middle];

            if (candidate == target) {
                return middle;
            }

            if (candidate < target) {
                left = middle + 1;
            } else {
                right = middle - 1;
            }
        }

        return NOT_FOUND;
    }

    private static int middleIndex(int left, int right) {
        return left + (right - left) / 2;
    }

    public static void main(String[] args) {
        BinarySearchDemo.main(args);
    }
}
