import java.util.Objects;

final class BinarySearch {
    static final int NOT_FOUND = -1;

    private BinarySearch() {
    }

    static int binarySearch(int[] values, int target) {
        return indexOf(values, target);
    }

    static int indexOf(int[] values, int target) {
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

final class BinarySearchDemo {
    private static final int[] SAMPLE_VALUES = {2, 3, 4, 10, 40};
    private static final int SAMPLE_TARGET = 10;

    private BinarySearchDemo() {
    }

    private static String describeSearchResult(int index) {
        if (index == BinarySearch.NOT_FOUND) {
            return "Element is not present in array";
        }

        return "Element is present at index " + index;
    }

    public static void main(String[] args) {
        int result = BinarySearch.indexOf(SAMPLE_VALUES, SAMPLE_TARGET);

        System.out.println(describeSearchResult(result));
    }
}
