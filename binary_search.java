class BinarySearch {
    private static final int NOT_FOUND = -1;
    private static final int[] SAMPLE_NUMBERS = { 2, 3, 4, 10, 40 };
    private static final int SAMPLE_TARGET = 10;

    static int binarySearch(int[] sortedNumbers, int target) {
        if (sortedNumbers == null) {
            throw new IllegalArgumentException("Array must not be null");
        }

        SearchBounds bounds = SearchBounds.forArray(sortedNumbers);

        while (bounds.hasRemainingValues()) {
            int middle = bounds.midpoint();

            if (targetMatchesMiddle(sortedNumbers, target, middle)) {
                return middle;
            }

            if (targetIsAfterMiddle(sortedNumbers, target, middle)) {
                bounds = bounds.after(middle);
            } else {
                bounds = bounds.before(middle);
            }
        }

        return NOT_FOUND;
    }

    private static boolean targetMatchesMiddle(int[] sortedNumbers, int target, int middle) {
        return sortedNumbers[middle] == target;
    }

    private static boolean targetIsAfterMiddle(int[] sortedNumbers, int target, int middle) {
        return sortedNumbers[middle] < target;
    }

    private static String formatSearchResult(int index) {
        if (index == NOT_FOUND) {
            return "Element is not present in array";
        }

        return "Element is present at index " + index;
    }

    public static void main(String[] args) {
        runDemo();
    }

    private static void runDemo() {
        int result = binarySearch(SAMPLE_NUMBERS, SAMPLE_TARGET);
        System.out.println(formatSearchResult(result));
    }

    private static final class SearchBounds {
        private final int left;
        private final int right;

        private SearchBounds(int left, int right) {
            this.left = left;
            this.right = right;
        }

        private static SearchBounds forArray(int[] sortedNumbers) {
            return new SearchBounds(0, sortedNumbers.length - 1);
        }

        private boolean hasRemainingValues() {
            return left <= right;
        }

        private int midpoint() {
            return left + (right - left) / 2;
        }

        private SearchBounds after(int middle) {
            return new SearchBounds(middle + 1, right);
        }

        private SearchBounds before(int middle) {
            return new SearchBounds(left, middle - 1);
        }
    }
}
