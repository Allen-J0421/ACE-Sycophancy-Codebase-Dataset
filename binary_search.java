final class BinarySearch {
    private static final int NOT_FOUND = -1;
    private static final int[] DEMO_ARRAY = { 2, 3, 4, 10, 40 };
    private static final int DEMO_TARGET = 10;

    private BinarySearch() {
    }

    static int binarySearch(int[] array, int target) {
        int lowerBound = 0;
        int upperBound = array.length - 1;

        while (lowerBound <= upperBound) {
            int middleIndex = midpoint(lowerBound, upperBound);
            int middleValue = array[middleIndex];

            if (middleValue == target) {
                return middleIndex;
            }

            if (middleValue < target) {
                lowerBound = middleIndex + 1;
            } else {
                upperBound = middleIndex - 1;
            }
        }

        return NOT_FOUND;
    }

    private static int midpoint(int lowerBound, int upperBound) {
        return lowerBound + (upperBound - lowerBound) / 2;
    }

    static String formatSearchResult(int result) {
        if (result == NOT_FOUND) {
            return "Element is not present in array";
        }

        return "Element is present at index " + result;
    }

    private static void printSearchResult(int result) {
        System.out.println(formatSearchResult(result));
    }

    public static void main(String[] args) {
        int result = binarySearch(DEMO_ARRAY, DEMO_TARGET);
        printSearchResult(result);
    }
}
