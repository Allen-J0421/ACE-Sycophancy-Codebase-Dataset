final class BinarySearch {
    private static final int NOT_FOUND = -1;
    private static final int[] SAMPLE_VALUES = { 2, 3, 4, 10, 40 };
    private static final int SAMPLE_TARGET = 10;
    private static final String NOT_FOUND_MESSAGE = "Element is not present in array";
    private static final String FOUND_MESSAGE_PREFIX = "Element is present at index ";

    private BinarySearch() {
    }

    static int binarySearch(final int[] values, final int target) {
        return findTargetIndex(values, target);
    }

    private static int findTargetIndex(final int[] values, final int target) {
        final SearchRange range = new SearchRange(values.length);

        while (range.hasCandidates()) {
            final int mid = range.midpoint();
            final int current = values[mid];

            if (isTarget(current, target)) {
                return mid;
            }

            if (isBelowTarget(current, target)) {
                range.discardLeftThrough(mid);
            } else {
                range.discardRightFrom(mid);
            }
        }

        return NOT_FOUND;
    }

    private static boolean isTarget(final int current, final int target) {
        return current == target;
    }

    private static boolean isBelowTarget(final int current, final int target) {
        return current < target;
    }

    private static String formatSearchResult(final int result) {
        if (result == NOT_FOUND) {
            return NOT_FOUND_MESSAGE;
        }

        return FOUND_MESSAGE_PREFIX + result;
    }

    public static void main(String[] args) {
        final int result = binarySearch(SAMPLE_VALUES, SAMPLE_TARGET);

        System.out.println(formatSearchResult(result));
    }

    private static final class SearchRange {
        private int left;
        private int right;

        private SearchRange(final int size) {
            left = 0;
            right = size - 1;
        }

        private boolean hasCandidates() {
            return left <= right;
        }

        private int midpoint() {
            return left + (right - left) / 2;
        }

        private void discardLeftThrough(final int index) {
            left = index + 1;
        }

        private void discardRightFrom(final int index) {
            right = index - 1;
        }
    }
}
