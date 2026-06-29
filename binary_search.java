class BinarySearch {
    private static final int NOT_FOUND = -1;
    private static final int DEMO_TARGET = 10;
    private static final String NOT_FOUND_MESSAGE = "Element is not present in array";
    private static final String FOUND_MESSAGE_PREFIX = "Element is present at index ";

    static int binarySearch(int[] array, int target) {
        int lowIndex = 0;
        int highIndex = array.length - 1;

        while (lowIndex <= highIndex) {
            int midIndex = midpoint(lowIndex, highIndex);
            int value = array[midIndex];

            if (value == target) {
                return midIndex;
            }

            if (value < target) {
                lowIndex = midIndex + 1;
            } else {
                highIndex = midIndex - 1;
            }
        }

        return NOT_FOUND;
    }

    private static int midpoint(int lowIndex, int highIndex) {
        return lowIndex + (highIndex - lowIndex) / 2;
    }

    private static String formatSearchResult(int result) {
        if (result == NOT_FOUND) {
            return NOT_FOUND_MESSAGE;
        }

        return FOUND_MESSAGE_PREFIX + result;
    }

    public static void main(String[] args) {
        runDemo();
    }

    private static void runDemo() {
        int result = binarySearch(demoArray(), DEMO_TARGET);

        System.out.println(formatSearchResult(result));
    }

    private static int[] demoArray() {
        return new int[] { 2, 3, 4, 10, 40 };
    }
}
