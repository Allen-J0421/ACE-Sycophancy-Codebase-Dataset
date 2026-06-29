final class BinarySearch {
    private static final int NOT_FOUND = -1;

    private BinarySearch() {
    }

    static int binarySearch(int[] values, int target) {
        if (values == null) {
            throw new IllegalArgumentException("values must not be null");
        }

        int low = 0;
        int high = values.length - 1;

        while (low <= high) {
            int middle = low + (high - low) / 2;
            int currentValue = values[middle];

            if (currentValue == target) {
                return middle;
            }

            if (currentValue < target) {
                low = middle + 1;
            } else {
                high = middle - 1;
            }
        }

        return NOT_FOUND;
    }

    public static void main(String[] args) {
        int[] values = { 2, 3, 4, 10, 40 };
        int target = 10;
        int result = binarySearch(values, target);

        System.out.println(formatSearchResult(result));
    }

    private static String formatSearchResult(int result) {
        if (result == NOT_FOUND) {
            return "Element is not present in array";
        }

        return "Element is present at index " + result;
    }
}
