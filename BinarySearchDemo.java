final class BinarySearchDemo {
    private static final int[] SAMPLE_VALUES = {2, 3, 4, 10, 40};
    private static final int SAMPLE_TARGET = 10;

    private BinarySearchDemo() {
    }

    static void run() {
        int result = BinarySearch.indexOf(SAMPLE_VALUES, SAMPLE_TARGET);
        System.out.println(resultMessage(result));
    }

    private static String resultMessage(int result) {
        if (BinarySearch.isFound(result)) {
            return "Element is present at index " + result;
        }

        return "Element is not present in array";
    }
}
