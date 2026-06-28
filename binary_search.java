final class BinarySearch {
    private static final int NOT_FOUND = -1;
    private static final int SAMPLE_TARGET = 10;

    private BinarySearch() {
    }

    static int binarySearch(int[] sortedValues, int target) {
        return indexOf(sortedValues, target);
    }

    static int indexOf(int[] sortedValues, int target) {
        requireValues(sortedValues);

        int low = 0;
        int high = sortedValues.length - 1;

        while (low <= high) {
            int midpoint = low + (high - low) / 2;
            int value = sortedValues[midpoint];

            if (value == target) {
                return midpoint;
            }

            if (value < target) {
                low = midpoint + 1;
            } else {
                high = midpoint - 1;
            }
        }

        return NOT_FOUND;
    }

    static boolean contains(int[] sortedValues, int target) {
        return indexOf(sortedValues, target) != NOT_FOUND;
    }

    private static void requireValues(int[] sortedValues) {
        if (sortedValues == null) {
            throw new IllegalArgumentException("sortedValues must not be null");
        }
    }

    public static void main(String[] args) {
        int index = indexOf(sampleValues(), SAMPLE_TARGET);

        System.out.println(formatSearchResult(index));
    }

    private static int[] sampleValues() {
        return new int[] {2, 3, 4, 10, 40};
    }

    private static String formatSearchResult(int index) {
        if (index == NOT_FOUND) {
            return "Element is not present in array";
        }

        return "Element is present at index " + index;
    }
}
