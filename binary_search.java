class BinarySearch {
    private static final int NOT_FOUND = -1;
    private static final int[] SAMPLE_NUMBERS = { 2, 3, 4, 10, 40 };
    private static final int SAMPLE_TARGET = 10;

    static int binarySearch(int[] sortedNumbers, int target) {
        if (sortedNumbers == null) {
            throw new IllegalArgumentException("Array must not be null");
        }

        int left = 0;
        int right = sortedNumbers.length - 1;

        while (left <= right) {
            int middle = midpoint(left, right);

            if (targetMatchesMiddle(sortedNumbers, target, middle)) {
                return middle;
            }

            if (targetIsAfterMiddle(sortedNumbers, target, middle)) {
                left = middle + 1;
            } else {
                right = middle - 1;
            }
        }

        return NOT_FOUND;
    }

    private static int midpoint(int left, int right) {
        return left + (right - left) / 2;
    }

    private static boolean targetMatchesMiddle(int[] sortedNumbers, int target, int middle) {
        return sortedNumbers[middle] == target;
    }

    private static boolean targetIsAfterMiddle(int[] sortedNumbers, int target, int middle) {
        return sortedNumbers[middle] < target;
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
        int result = binarySearch(SAMPLE_NUMBERS, SAMPLE_TARGET);
        System.out.println(formatSearchResult(result));
    }
}
