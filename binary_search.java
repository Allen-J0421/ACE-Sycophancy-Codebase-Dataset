class BinarySearch {
    private static final int NOT_FOUND = -1;

    /**
     * Returns the index of target in a sorted ascending array, or -1 when absent.
     */
    static int binarySearch(int[] values, int target) {
        int left = 0;
        int right = values.length - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;
            int midValue = values[mid];

            if (midValue == target) {
                return mid;
            }

            if (midValue < target) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        return NOT_FOUND;
    }

    public static void main(String[] args) {
        runDemo();
    }

    private static void runDemo() {
        int[] values = { 2, 3, 4, 10, 40 };
        int target = 10;
        int index = binarySearch(values, target);

        System.out.println(formatSearchResult(index));
    }

    private static String formatSearchResult(int index) {
        if (index == NOT_FOUND) {
            return "Element is not present in array";
        }

        return "Element is present at index " + index;
    }
}
