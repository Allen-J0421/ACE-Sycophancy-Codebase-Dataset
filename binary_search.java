class BinarySearch {
    private static final int NOT_FOUND = -1;
    private static final int[] DEMO_NUMBERS = { 2, 3, 4, 10, 40 };
    private static final int DEMO_TARGET = 10;

    static int binarySearch(int[] sortedNumbers, int target) {
        int low = 0;
        int high = sortedNumbers.length - 1;

        while (low <= high) {
            int mid = low + (high - low) / 2;
            int value = sortedNumbers[mid];

            if (value == target) {
                return mid;
            }

            if (value < target) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        return NOT_FOUND;
    }

    public static void main(String[] args) {
        int result = binarySearch(DEMO_NUMBERS, DEMO_TARGET);

        System.out.println(formatSearchResult(result));
    }

    private static String formatSearchResult(int result) {
        if (result == NOT_FOUND) {
            return "Element is not present in array";
        }

        return "Element is present at index " + result;
    }
}
