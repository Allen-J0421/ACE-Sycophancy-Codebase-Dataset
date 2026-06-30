final class BinarySearch {
    private static final int DEMO_TARGET = 10;
    private static final int NOT_FOUND = -1;
    private static final String NOT_FOUND_MESSAGE = "Element is not present in array";
    private static final String FOUND_MESSAGE_PREFIX = "Element is present at index ";

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
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        return NOT_FOUND;
    }

    private static int midpoint(int low, int high) {
        return low + (high - low) / 2;
    }

    public static void main(String[] args) {
        System.out.println(runDemo());
    }

    private static String runDemo() {
        int result = binarySearch(demoNumbers(), DEMO_TARGET);

        return formatSearchResult(result);
    }

    private static int[] demoNumbers() {
        return new int[] { 2, 3, 4, 10, 40 };
    }

    private static String formatSearchResult(int index) {
        if (index == NOT_FOUND) {
            return NOT_FOUND_MESSAGE;
        }

        return FOUND_MESSAGE_PREFIX + index;
    }
}
