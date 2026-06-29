final class BinarySearch {
    private static final int NOT_FOUND = -1;

    private BinarySearch() {
    }

    static int binarySearch(final int[] sortedValues, final int target) {
        int low = 0;
        int high = sortedValues.length - 1;

        while (low <= high) {
            final int mid = low + (high - low) / 2;
            final int midValue = sortedValues[mid];

            if (midValue == target) {
                return mid;
            }

            if (midValue < target) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        return NOT_FOUND;
    }

    public static void main(final String[] args) {
        final int[] sortedValues = { 2, 3, 4, 10, 40 };
        final int target = 10;
        final int result = binarySearch(sortedValues, target);

        printSearchResult(result);
    }

    private static void printSearchResult(final int index) {
        if (index == NOT_FOUND) {
            System.out.println("Element is not present in array");
        } else {
            System.out.println("Element is present at index " + index);
        }
    }
}
