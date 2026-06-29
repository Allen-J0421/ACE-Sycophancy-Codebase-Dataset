final class BinarySearch {
    private static final int NOT_FOUND = -1;
    private static final int[] SAMPLE_VALUES = { 2, 3, 4, 10, 40 };
    private static final int SAMPLE_TARGET = 10;

    private BinarySearch() {
    }

    static int binarySearch(int[] sortedValues, int target) {
        int lowerBound = 0;
        int upperBound = sortedValues.length - 1;

        while (lowerBound <= upperBound) {
            int candidateIndex = midpoint(lowerBound, upperBound);
            int candidateValue = sortedValues[candidateIndex];

            if (candidateValue == target) {
                return candidateIndex;
            }

            if (candidateValue < target) {
                lowerBound = candidateIndex + 1;
            } else {
                upperBound = candidateIndex - 1;
            }
        }

        return NOT_FOUND;
    }

    public static void main(String[] args) {
        runSampleSearch();
    }

    private static void runSampleSearch() {
        int result = binarySearch(SAMPLE_VALUES, SAMPLE_TARGET);

        System.out.println(formatResultMessage(result));
    }

    private static int midpoint(int lowerBound, int upperBound) {
        return lowerBound + (upperBound - lowerBound) / 2;
    }

    private static String formatResultMessage(int index) {
        if (!isFound(index)) {
            return "Element is not present in array";
        }

        return "Element is present at index " + index;
    }

    private static boolean isFound(int index) {
        return index != NOT_FOUND;
    }
}
