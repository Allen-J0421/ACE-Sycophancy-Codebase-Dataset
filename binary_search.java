final class BinarySearch {
    private BinarySearch() {
    }

    static int binarySearch(int[] values, int target) {
        int low = 0;
        int high = values.length - 1;

        while (low <= high) {
            int mid = low + (high - low) / 2;
            int current = values[mid];

            if (current == target) {
                return mid;
            }

            if (current < target) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        return -1;
    }

    public static void main(String[] args) {
        int[] values = { 2, 3, 4, 10, 40 };
        int target = 10;
        int result = binarySearch(values, target);

        printSearchResult(result);
    }

    private static void printSearchResult(int result) {
        if (result == -1) {
            System.out.println("Element is not present in array");
        } else {
            System.out.println("Element is present at index " + result);
        }
    }
}
