class BinarySearch {
    private static final int NOT_FOUND = -1;

    private BinarySearch() {
    }

    static int binarySearch(int[] sortedValues, int target) {
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

        return NOT_FOUND;
    }

    static boolean isFound(int resultIndex) {
        return resultIndex != NOT_FOUND;
    }

    private static int middleIndex(int left, int right) {
        return left + (right - left) / 2;
    }

    public static void main(String[] args) {
        BinarySearchDemo.run();
    }
}

class BinarySearchDemo {
    private static final int[] SORTED_VALUES = { 2, 3, 4, 10, 40 };
    private static final int TARGET = 10;

    private BinarySearchDemo() {
    }

    static void run() {
        int result = BinarySearch.binarySearch(SORTED_VALUES, TARGET);

        System.out.println(formatSearchResult(result));
    }

    private static String formatSearchResult(int resultIndex) {
        if (!BinarySearch.isFound(resultIndex)) {
            return "Element is not present in array";
        }

        return "Element is present at index " + resultIndex;
    }
}
