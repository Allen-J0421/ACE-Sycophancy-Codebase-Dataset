import java.util.Objects;

public final class BinarySearch {
    public static final int NOT_FOUND_INDEX = SearchResult.NOT_FOUND_INDEX;

    private BinarySearch() {
    }

    public static SearchResult search(int[] sortedValues, int target) {
        return SearchResult.fromIndex(binarySearch(sortedValues, target));
    }

    public static int binarySearch(int[] sortedValues, int target) {
        Objects.requireNonNull(sortedValues, "sortedValues");

        int left = 0;
        int right = sortedValues.length - 1;

        while (left <= right) {
            int middle = middleIndex(left, right);
            int candidate = sortedValues[middle];

            if (candidate == target) {
                return middle;
            }

            if (candidate < target) {
                left = middle + 1;
            } else {
                right = middle - 1;
            }
        }

        return NOT_FOUND_INDEX;
    }

    private static int middleIndex(int left, int right) {
        return left + (right - left) / 2;
    }

    public static void main(String[] args) {
        BinarySearchDemo.run();
    }
}
