import java.util.OptionalInt;

final class BinarySearch {
    private static final int NOT_FOUND = -1;
    private static final int[] SAMPLE_VALUES = { 2, 3, 4, 10, 40 };
    private static final int SAMPLE_TARGET = 10;

    private BinarySearch() {
    }

    static int binarySearch(int[] arr, int target) {
        return findIndex(arr, target).orElse(NOT_FOUND);
    }

    static OptionalInt findIndex(int[] arr, int target) {
        int low = 0;
        int high = arr.length - 1;

        while (low <= high) {
            int middleIndex = low + (high - low) / 2;
            int middleValue = arr[middleIndex];

            if (middleValue == target) {
                return OptionalInt.of(middleIndex);
            }

            if (middleValue < target) {
                low = middleIndex + 1;
            } else {
                high = middleIndex - 1;
            }
        }

        return OptionalInt.empty();
    }

    private static String formatSearchResult(OptionalInt result) {
        return result.isEmpty()
                ? "Element is not present in array"
                : "Element is present at index " + result.getAsInt();
    }

    public static void main(String[] args) {
        OptionalInt result = findIndex(SAMPLE_VALUES, SAMPLE_TARGET);

        System.out.println(formatSearchResult(result));
    }
}
