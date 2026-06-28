final class BinarySearch {
    private BinarySearch() {
    }

    static int binarySearch(int[] sortedValues, int target) {
        int low = 0;
        int high = sortedValues.length - 1;

        while (low <= high) {
            int mid = low + (high - low) / 2;
            int value = sortedValues[mid];

            if (value == target) {
                return mid;
            }

            if (value < target) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        return -1;
    }

    public static void main(String[] args) {
        int[] sortedValues = { 2, 3, 4, 10, 40 };
        int target = 10;
        int result = binarySearch(sortedValues, target);

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
