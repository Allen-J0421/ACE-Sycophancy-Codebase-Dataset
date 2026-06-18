import java.util.Objects;

public final class BinarySearch {
    public static final int NOT_FOUND = -1;

    private BinarySearch() {
    }

    public static int binarySearch(int[] sortedValues, int target) {
        return indexOf(sortedValues, target);
    }

    public static int indexOf(int[] sortedValues, int target) {
        Objects.requireNonNull(sortedValues, "sortedValues");

        int left = 0;
        int right = sortedValues.length;

        while (left < right) {
            int middle = middleIndex(left, right);
            int candidate = sortedValues[middle];

            if (candidate == target) {
                return middle;
            }

            if (candidate < target) {
                left = middle + 1;
            } else {
                right = middle;
            }
        }

        return NOT_FOUND;
    }

    private static int middleIndex(int left, int right) {
        return left + (right - left) / 2;
    }

    public static void main(String[] args) {
        BinarySearchDemo.run();
    }
}
