class BinarySearch {
    private static final int NOT_FOUND = -1;

    static int binarySearch(int[] values, int target) {
        int low = 0;
        int high = values.length - 1;

        while (low <= high) {
            int middle = midpoint(low, high);
            int current = values[middle];

            if (current == target) {
                return middle;
            }

            if (current < target) {
                low = middle + 1;
            } else {
                high = middle - 1;
            }
        }

        return NOT_FOUND;
    }

    public static void main(String[] args) {
        int[] values = {2, 3, 4, 10, 40};
        int target = 10;
        int result = binarySearch(values, target);

        System.out.println(formatResult(result));
    }

    private static int midpoint(int low, int high) {
        return low + (high - low) / 2;
    }

    private static String formatResult(int result) {
        if (!found(result)) {
            return "Element is not present in array";
        }

        return "Element is present at index " + result;
    }

    private static boolean found(int result) {
        return result != NOT_FOUND;
    }
}
