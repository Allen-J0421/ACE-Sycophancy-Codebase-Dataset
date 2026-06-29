final class BinarySearch {
    private static final int NOT_FOUND = -1;
    private static final String NOT_FOUND_MESSAGE = "Element is not present in array";
    private static final String FOUND_MESSAGE_PREFIX = "Element is present at index ";

    private BinarySearch() {
    }

    static int binarySearch(int[] sortedValues, int target) {
        int lowerBound = 0;
        int upperBound = sortedValues.length - 1;

        while (lowerBound <= upperBound) {
            int middleIndex = middleIndex(lowerBound, upperBound);
            int middleValue = sortedValues[middleIndex];

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

    private static int middleIndex(int lowerBound, int upperBound) {
        return lowerBound + (upperBound - lowerBound) / 2;
    }

    public static void main(String[] args) {
        int[] sortedValues = { 2, 3, 4, 10, 40 };
        int target = 10;
        int result = binarySearch(sortedValues, target);

        System.out.println(searchResultMessage(result));
    }

    private static String searchResultMessage(int result) {
        if (result == NOT_FOUND) {
            return NOT_FOUND_MESSAGE;
        }

        return FOUND_MESSAGE_PREFIX + result;
    }
}
