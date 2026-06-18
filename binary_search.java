final class BinarySearch {
    private static final int[] SAMPLE_ARRAY = { 2, 3, 4, 10, 40 };
    private static final int SAMPLE_TARGET = 10;

    private BinarySearch() {
        // Utility class.
    }

    static int binarySearch(int[] arr, int target) {
        int left = 0;
        int right = arr.length - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;
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

        return -1;
    }

    public static void main(String[] args) {
        int result = binarySearch(SAMPLE_ARRAY, SAMPLE_TARGET);

        if (result == -1) {
            System.out.println("Element is not present in array");
            return;
        }

        System.out.println("Element is present at index " + result);
    }
}
