final class BinarySearch {
    private static final int[] DEMO_NUMBERS = { 2, 3, 4, 10, 40 };
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
            int mid = low + (high - low) / 2;

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

    public static void main(String[] args) {
        System.out.println(runDemo());
    }

    private static String runDemo() {
        int[] numbers = DEMO_NUMBERS;
        int target = DEMO_TARGET;
        int result = binarySearch(numbers, target);

        return formatSearchResult(result);
    }

    private static String formatSearchResult(int index) {
        if (index == NOT_FOUND) {
            return NOT_FOUND_MESSAGE;
        }

        return FOUND_MESSAGE_PREFIX + index;
    }
}
