final class BinarySearch {
    private BinarySearch() {
    }

    static int binarySearch(int[] arr, int target) {
        int low = 0;
        int high = arr.length - 1;

        while (low <= high) {
            int middleIndex = low + (high - low) / 2;
            int middleValue = arr[middleIndex];

            if (middleValue == target) {
                return middleIndex;
            }

            if (middleValue < target) {
                low = middleIndex + 1;
            } else {
                high = middleIndex - 1;
            }
        }

        return -1;
    }

    private static void printSearchResult(int result) {
        if (result == -1) {
            System.out.println("Element is not present in array");
        } else {
            System.out.println("Element is present at index " + result);
        }
    }

    public static void main(String[] args) {
        int[] arr = { 2, 3, 4, 10, 40 };
        int target = 10;
        int result = binarySearch(arr, target);

        printSearchResult(result);
    }
}
