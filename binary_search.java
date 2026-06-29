class BinarySearch {
    private static final int NOT_FOUND = -1;
    private static final int[] SAMPLE_VALUES = { 2, 3, 4, 10, 40 };
    private static final int SAMPLE_TARGET = 10;

    private BinarySearch() {
    }

    public static int binarySearch(int[] arr, int target) {
        if (isEmpty(arr)) {
            return NOT_FOUND;
        }

        int left = 0;
        int right = arr.length - 1;

        while (left <= right) {
            int mid = midpoint(left, right);
            int value = arr[mid];

            if (value == target) {
                return mid;
            }

            if (value < target) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        return NOT_FOUND;
    }

    private static boolean isEmpty(int[] arr) {
        return arr == null || arr.length == 0;
    }

    private static int midpoint(int left, int right) {
        return left + (right - left) / 2;
    }

    private static String formatSearchResult(int target, int result) {
        if (result == NOT_FOUND) {
            return target + " is not present in array";
        }

        return target + " is present at index " + result;
    }

    public static void main(String[] args) {
        int result = binarySearch(SAMPLE_VALUES, SAMPLE_TARGET);

        System.out.println(formatSearchResult(SAMPLE_TARGET, result));
    }
}
