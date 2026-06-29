public final class BinarySearch {
    private static final int NOT_FOUND = -1;

    private BinarySearch() {
    }

    static SearchResult search(int[] sortedValues, int target) {
        int resultIndex = binarySearch(sortedValues, target);

        if (isFound(resultIndex)) {
            return SearchResult.foundAt(resultIndex);
        }

        return SearchResult.notFound();
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
