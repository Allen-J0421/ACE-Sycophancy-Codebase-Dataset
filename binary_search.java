class BinarySearch {
    private static final int NOT_FOUND = -1;

    static int binarySearch(int[] arr, int target) {
        if (arr == null || arr.length == 0) {
            return NOT_FOUND;
        }

        int low = 0;
        int high = arr.length - 1;

        while (low <= high) {
            int mid = low + (high - low) / 2;
            int value = arr[mid];

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

    private static String formatSearchResult(int result) {
        if (result == NOT_FOUND) {
            return "Element is not present in array";
        }

        return "Element is present at index " + result;
    }

    public static void main(String[] args) {
        int[] arr = { 2, 3, 4, 10, 40 };
        int target = 10;
        int result = binarySearch(arr, target);

        System.out.println(formatSearchResult(result));
    }
}
