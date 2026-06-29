class BinarySearch {
    private static final int NOT_FOUND = -1;

    static int binarySearch(int[] array, int target) {
        int low = 0;
        int high = array.length - 1;

        while (low <= high) {
            int mid = low + (high - low) / 2;
            int value = array[mid];

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

    private static void printSearchResult(int result) {
        if (result == NOT_FOUND) {
            System.out.println("Element is not present in array");
        } else {
            System.out.println("Element is present at index " + result);
        }
    }

    public static void main(String[] args) {
        int[] array = { 2, 3, 4, 10, 40 };
        int target = 10;
        int result = binarySearch(array, target);

        printSearchResult(result);
    }
}
