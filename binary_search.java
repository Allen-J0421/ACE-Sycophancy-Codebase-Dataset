class BinarySearch {
    private static final int NOT_FOUND = -1;
    private static final int[] SAMPLE_NUMBERS = { 2, 3, 4, 10, 40 };
    private static final int SAMPLE_TARGET = 10;

    private BinarySearch() {
    }

    static int binarySearch(int[] numbers, int target) {
        int low = 0;
        int high = numbers.length - 1;

        while (low <= high) {
            int mid = midpoint(low, high);

            if (numbers[mid] == target) {
                return mid;
            }

            if (numbers[mid] < target) {
                low = nextLowerBound(mid);
            } else {
                high = nextUpperBound(mid);
            }
        }

        return NOT_FOUND;
    }

    private static int midpoint(int low, int high) {
        return low + (high - low) / 2;
    }

    private static int nextLowerBound(int mid) {
        return mid + 1;
    }

    private static int nextUpperBound(int mid) {
        return mid - 1;
    }

    private static String formatSearchResult(int index) {
        if (index == NOT_FOUND) {
            return "Element is not present in array";
        }

        return "Element is present at index " + index;
    }

    private static void printSearchResult(int index) {
        System.out.println(formatSearchResult(index));
    }

    public static void main(String[] args) {
        int result = binarySearch(SAMPLE_NUMBERS, SAMPLE_TARGET);

        printSearchResult(result);
    }
}
