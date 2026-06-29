class BinarySearch {
    private static final int[] SAMPLE_NUMBERS = {2, 3, 4, 10, 40};
    private static final int SAMPLE_TARGET = 10;
    private static final int NOT_FOUND = -1;

    private BinarySearch() {
    }

    static int binarySearch(int[] numbers, int target) {
        int low = 0;
        int high = numbers.length - 1;

        while (low <= high) {
            int mid = low + (high - low) / 2;
            int value = numbers[mid];

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

    private static void printSearchResult(int index) {
        if (index == NOT_FOUND) {
            System.out.println("Element is not present in array");
        } else {
            System.out.println("Element is present at index " + index);
        }
    }

    public static void main(String[] args) {
        int result = binarySearch(SAMPLE_NUMBERS, SAMPLE_TARGET);
        printSearchResult(result);
    }
}
