final class BinarySearch {
    private static final int NOT_FOUND = -1;
    private static final int[] DEMO_SORTED_ARRAY = { 2, 3, 4, 10, 40 };
    private static final int DEMO_TARGET = 10;
    private static final String NOT_FOUND_MESSAGE = "Element is not present in array";
    private static final String FOUND_MESSAGE_PREFIX = "Element is present at index ";

    private BinarySearch() {
    }

    static int binarySearch(int[] sortedArray, int target) {
        SearchBounds bounds = SearchBounds.forArray(sortedArray);

        while (bounds.hasCandidates()) {
            int mid = bounds.midpoint();
            SearchDecision decision = decideAt(sortedArray, mid, target);

            switch (decision) {
                case FOUND:
                    return mid;
                case TARGET_AFTER_MIDPOINT:
                    bounds.discardLowerHalf(mid);
                    break;
                case TARGET_BEFORE_MIDPOINT:
                    bounds.discardUpperHalf(mid);
                    break;
            }
        }

        return NOT_FOUND;
    }

    public static void main(String[] args) {
        runDemo();
    }

    private static SearchDecision decideAt(int[] sortedArray, int index, int target) {
        return SearchDecision.fromComparison(Integer.compare(sortedArray[index], target));
    }

    private static void runDemo() {
        int result = binarySearch(DEMO_SORTED_ARRAY, DEMO_TARGET);

        System.out.println(formatSearchResult(result));
    }

    private static String formatSearchResult(int result) {
        if (result == NOT_FOUND) {
            return NOT_FOUND_MESSAGE;
        }

        return FOUND_MESSAGE_PREFIX + result;
    }

    private enum SearchDecision {
        FOUND,
        TARGET_BEFORE_MIDPOINT,
        TARGET_AFTER_MIDPOINT;

        private static SearchDecision fromComparison(int comparison) {
            if (comparison == 0) {
                return FOUND;
            }

            if (comparison < 0) {
                return TARGET_AFTER_MIDPOINT;
            }

            return TARGET_BEFORE_MIDPOINT;
        }
    }

    private static final class SearchBounds {
        private int low;
        private int high;

        private SearchBounds(int low, int high) {
            this.low = low;
            this.high = high;
        }

        private static SearchBounds forArray(int[] array) {
            return new SearchBounds(0, array.length - 1);
        }

        private boolean hasCandidates() {
            return low <= high;
        }

        private int midpoint() {
            return low + (high - low) / 2;
        }

        private void discardLowerHalf(int mid) {
            low = mid + 1;
        }

        private void discardUpperHalf(int mid) {
            high = mid - 1;
        }
    }
}
