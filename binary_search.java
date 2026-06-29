class BinarySearch {
    private static final int NOT_FOUND = -1;
    private static final int[] SAMPLE_NUMBERS = { 2, 3, 4, 10, 40 };
    private static final int SAMPLE_TARGET = 10;

    static int binarySearch(int[] sortedNumbers, int target) {
        if (sortedNumbers == null) {
            throw new IllegalArgumentException("Array must not be null");
        }

        SearchInput input = new SearchInput(sortedNumbers, target);
        SearchBounds bounds = input.bounds();

        while (bounds.hasRemainingValues()) {
            int middle = bounds.midpoint();

            if (input.matches(middle)) {
                return middle;
            }

            bounds = input.nextBounds(bounds, middle);
        }

        return NOT_FOUND;
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

    private static final class SearchInput {
        private final int[] sortedNumbers;
        private final int target;

        private SearchInput(int[] sortedNumbers, int target) {
            this.sortedNumbers = sortedNumbers;
            this.target = target;
        }

        private boolean matches(int index) {
            return sortedNumbers[index] == target;
        }

        private SearchBounds nextBounds(SearchBounds currentBounds, int middle) {
            if (targetIsAfter(middle)) {
                return currentBounds.after(middle);
            }

            return currentBounds.before(middle);
        }

        private SearchBounds bounds() {
            return new SearchBounds(0, sortedNumbers.length - 1);
        }

        private boolean targetIsAfter(int index) {
            return sortedNumbers[index] < target;
        }
    }

    private static final class SearchBounds {
        private final int left;
        private final int right;

        private SearchBounds(int left, int right) {
            this.left = left;
            this.right = right;
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
