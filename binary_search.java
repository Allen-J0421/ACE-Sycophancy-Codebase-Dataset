class BinarySearch {
    private static final int[] SORTED_SAMPLE_NUMBERS = {2, 3, 4, 10, 40};
    private static final int SAMPLE_TARGET = 10;
    private static final int NOT_FOUND = -1;

    private BinarySearch() {
    }

    static int indexOf(int[] numbers, int target) {
        if (numbers == null) {
            throw new IllegalArgumentException("numbers must not be null");
        }

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

    private static String formatSearchResult(int index) {
        if (index == NOT_FOUND) {
            return "Element is not present in array";
        }

        return "Element is present at index " + index;
    }

    public static void main(String[] args) {
        runDemo();
    }

    private static void runDemo() {
        int result = indexOf(SORTED_SAMPLE_NUMBERS, SAMPLE_TARGET);
        System.out.println(formatSearchResult(result));
    }
}
