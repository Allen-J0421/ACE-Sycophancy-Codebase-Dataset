final class BinarySearch {
    private static final int NOT_FOUND = -1;

    private BinarySearch() {
    }

    static int binarySearch(int[] sortedNumbers, int target) {
        int low = 0;
        int high = sortedNumbers.length - 1;

        while (low <= high) {
            final int middle = midpoint(low, high);
            final int middleValue = sortedNumbers[middle];

            if (middleValue == target) {
                return middle;
            }

            if (middleValue < target) {
                low = middle + 1;
            } else {
                high = middle - 1;
            }
        }

        return NOT_FOUND;
    }

    private static int midpoint(int low, int high) {
        return low + (high - low) / 2;
    }

    private static String formatSearchResult(int index) {
        if (index == NOT_FOUND) {
            return "Element is not present in array";
        }

        return "Element is present at index " + index;
    }

    public static void main(String[] args) {
        final int[] sortedNumbers = { 2, 3, 4, 10, 40 };
        final int target = 10;
        final int result = binarySearch(sortedNumbers, target);

        System.out.println(formatSearchResult(result));
    }
}
