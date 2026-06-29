final class BinarySearch {
    private static final int NOT_FOUND = -1;
    private static final int[] SAMPLE_VALUES = { 2, 3, 4, 10, 40 };
    private static final int SAMPLE_TARGET = 10;
    private static final String NOT_FOUND_MESSAGE = "Element is not present in array";
    private static final String FOUND_MESSAGE_TEMPLATE = "Element is present at index %d";

    private BinarySearch() {
    }

    static int binarySearch(final int[] values, final int target) {
        return findTargetIndex(values, target);
    }

    private static int findTargetIndex(final int[] values, final int target) {
        final SearchRange range = SearchRange.forSize(values.length);

        while (range.hasCandidates()) {
            final int mid = range.midpoint();
            final int current = values[mid];
            final int comparison = Integer.compare(current, target);

            if (comparison == 0) {
                return mid;
            }

            if (comparison < 0) {
                range.discardLeftThrough(mid);
            } else {
                range.discardRightFrom(mid);
            }
        }

        return NOT_FOUND;
    }

    private static String formatSearchResult(final int result) {
        if (result == NOT_FOUND) {
            return NOT_FOUND_MESSAGE;
        }

        return String.format(FOUND_MESSAGE_TEMPLATE, result);
    }

    public static void main(String[] args) {
        final int result = binarySearch(SAMPLE_VALUES, SAMPLE_TARGET);

        System.out.println(formatSearchResult(result));
    }

    private static final class SearchRange {
        private int left;
        private int right;

        private static SearchRange forSize(final int size) {
            return new SearchRange(0, size - 1);
        }

        private SearchRange(final int left, final int right) {
            this.left = left;
            this.right = right;
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
