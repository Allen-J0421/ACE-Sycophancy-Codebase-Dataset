final class BinarySearch {
    private static final int NOT_FOUND = -1;
    private static final int DEMO_TARGET = 10;

    private BinarySearch() {
    }

    static int binarySearch(int[] sortedValues, int target) {
        validateInput(sortedValues);

        SearchBounds bounds = new SearchBounds(sortedValues.length);

        while (bounds.hasCandidates()) {
            int mid = bounds.midpoint();

            if (sortedValues[mid] == target) {
                return mid;
            }

            if (sortedValues[mid] < target) {
                bounds.discardLowerHalf(mid);
            } else {
                bounds.discardUpperHalf(mid);
            }
        }

        return NOT_FOUND;
    }

    private static void validateInput(int[] sortedValues) {
        if (sortedValues == null) {
            throw new IllegalArgumentException("Input array cannot be null");
        }
    }

    private static String formatSearchResult(int index) {
        if (index == NOT_FOUND) {
            return "Element is not present in array";
        }

        return "Element is present at index " + index;
    }

    private static void printSearchResult(int index) {
        System.out.println(formatSearchResult(index));
    }

    private static int[] demoValues() {
        return new int[] {2, 3, 4, 10, 40};
    }

    public static void main(String[] args) {
        printSearchResult(binarySearch(demoValues(), DEMO_TARGET));
    }

    private static final class SearchBounds {
        private int low;
        private int high;

        private SearchBounds(int size) {
            low = 0;
            high = size - 1;
        }

        private boolean hasCandidates() {
            return low <= high;
        }

        private int midpoint() {
            return low + (high - low) / 2;
        }

        private void discardLowerHalf(int midpoint) {
            low = midpoint + 1;
        }

        private void discardUpperHalf(int midpoint) {
            high = midpoint - 1;
        }
    }
}
