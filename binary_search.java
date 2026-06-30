final class BinarySearch {
    private static final int NOT_FOUND = -1;
    private static final String FOUND_MESSAGE = "Element is present at index ";
    private static final String NOT_FOUND_MESSAGE = "Element is not present in array";

    private BinarySearch() {
    }

    static int binarySearch(int[] sortedArray, int target) {
        if (sortedArray == null) {
            throw new IllegalArgumentException("sortedArray must not be null");
        }

        int low = 0;
        int high = sortedArray.length - 1;

        while (low <= high) {
            int mid = low + (high - low) / 2;

            if (sortedArray[mid] == target) {
                return mid;
            }

            if (sortedArray[mid] < target) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        return NOT_FOUND;
    }

    private static String formatSearchResult(int result) {
        if (result == NOT_FOUND) {
            return NOT_FOUND_MESSAGE;
        }

        return FOUND_MESSAGE + result;
    }

    public static void main(String[] args) {
        int[] sortedArray = { 2, 3, 4, 10, 40 };
        int target = 10;
        int result = binarySearch(sortedArray, target);

        System.out.println(formatSearchResult(result));
    }
}
