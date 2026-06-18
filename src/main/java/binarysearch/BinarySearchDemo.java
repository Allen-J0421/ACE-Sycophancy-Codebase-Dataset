package binarysearch;

import java.util.OptionalInt;

public final class BinarySearchDemo {
    private static final int[] SAMPLE_ARRAY = { 2, 3, 4, 10, 40 };
    private static final int SAMPLE_TARGET = 10;
    private static final String NOT_FOUND_MESSAGE = "Element is not present in array";
    private static final String FOUND_MESSAGE_PREFIX = "Element is present at index ";

    private BinarySearchDemo() {
        // Demo utility class.
    }

    public static void main(String[] args) {
        OptionalInt result = BinarySearch.findIndex(SAMPLE_ARRAY, SAMPLE_TARGET);
        System.out.println(formatResult(result));
    }

    private static String formatResult(OptionalInt result) {
        if (result.isEmpty()) {
            return NOT_FOUND_MESSAGE;
        }

        return FOUND_MESSAGE_PREFIX + result.getAsInt();
    }
}
