class BinarySearch {
    private static final int NOT_FOUND = -1;

    static int binarySearch(int[] sortedNumbers, int target) {
        int low = 0;
        int high = sortedNumbers.length - 1;

        while (low <= high) {
            int mid = low + (high - low) / 2;
            int value = sortedNumbers[mid];

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
        int[] sortedNumbers = { 2, 3, 4, 10, 40 };
        int target = 10;
        int result = binarySearch(sortedNumbers, target);

        printSearchResult(result);
    }

    private static void printSearchResult(int result) {
        if (result == NOT_FOUND) {
            System.out.println("Element is not present in array");
        } else {
            System.out.println("Element is present at index " + result);
        }
    }
}
