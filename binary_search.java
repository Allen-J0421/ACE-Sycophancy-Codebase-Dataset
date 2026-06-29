class BinarySearch {
    private static final int NOT_FOUND = -1;
    private static final int[] SAMPLE_NUMBERS = { 2, 3, 4, 10, 40 };
    private static final int SAMPLE_TARGET = 10;

    private BinarySearch() {
    }

    /**
     * Returns the index of {@code target} in a sorted array, or {@code -1} when absent.
     */
    public static int indexOf(int[] numbers, int target) {
        int left = 0;
        int right = numbers.length - 1;

        while (left <= right) {
            int middle = midpoint(left, right);

            if (numbers[middle] == target) {
                return middle;
            }

            if (numbers[middle] < target) {
                left = middle + 1;
            } else {
                right = middle - 1;
            }
        }

        return NOT_FOUND;
    }

    /**
     * @see #indexOf(int[], int)
     */
    public static int binarySearch(int[] numbers, int target) {
        return indexOf(numbers, target);
    }

    private static int midpoint(int left, int right) {
        return left + (right - left) / 2;
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

    public static void main(String[] args) {
        int result = indexOf(SAMPLE_NUMBERS, SAMPLE_TARGET);

        printSearchResult(result);
    }
}
