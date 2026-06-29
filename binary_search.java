final class BinarySearch {
    private static final int NOT_FOUND = -1;

    private BinarySearch() {
    }

    static int binarySearch(int[] sortedValues, int target) {
        int low = 0;
        int high = sortedValues.length - 1;

        while (low <= high) {
            int mid = midpoint(low, high);

            if (sortedValues[mid] == target) {
                return mid;
            }

            if (sortedValues[mid] < target) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        return NOT_FOUND;
    }

    private static int midpoint(int low, int high) {
        return low + (high - low) / 2;
    }

    public static void main(String[] args) {
        int[] values = { 2, 3, 4, 10, 40 };
        int target = 10;

        int result = binarySearch(values, target);
        System.out.println(formatSearchResult(result));
    }

    private static String formatSearchResult(int result) {
        if (result == NOT_FOUND) {
            return "Element is not present in array";
        }

        return "Element is present at index " + result;
    }
}
