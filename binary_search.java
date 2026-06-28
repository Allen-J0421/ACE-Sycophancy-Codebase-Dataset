class BinarySearch {
    private static final int NOT_FOUND = -1;

    static int binarySearch(int[] sortedArray, int target) {
        int low = 0;
        int high = sortedArray.length - 1;

        while (low <= high) {
            int mid = midpoint(low, high);
            int value = sortedArray[mid];

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
        runDemo();
    }

    private static int midpoint(int low, int high) {
        return low + (high - low) / 2;
    }

    private static void runDemo() {
        int[] sortedArray = { 2, 3, 4, 10, 40 };
        int target = 10;
        int result = binarySearch(sortedArray, target);

        System.out.println(formatSearchResult(result));
    }

    private static String formatSearchResult(int result) {
        if (result == NOT_FOUND) {
            return "Element is not present in array";
        }

        return "Element is present at index " + result;
    }
}
