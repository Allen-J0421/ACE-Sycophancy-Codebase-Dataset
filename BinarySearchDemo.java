public final class BinarySearchDemo {
    private static final String FOUND_MESSAGE_PREFIX = "Element is present at index ";
    private static final String NOT_FOUND_MESSAGE = "Element is not present in array";

    private BinarySearchDemo() {
    }

    public static void main(String[] args) {
        run();
    }

    public static void run() {
        final int[] numbers = {2, 3, 4, 10, 40};
        final int target = 10;
        final int result = BinarySearch.indexOf(numbers, target);

        System.out.println(formatSearchResult(result));
    }

    private static String formatSearchResult(int index) {
        if (index == BinarySearch.NOT_FOUND) {
            return NOT_FOUND_MESSAGE;
        }

        return FOUND_MESSAGE_PREFIX + index;
    }
}
