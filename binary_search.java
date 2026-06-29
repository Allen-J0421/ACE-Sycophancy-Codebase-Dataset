final class BinarySearch {
    private static final int NOT_FOUND = -1;
    private static final int DEMO_TARGET = 10;

    private BinarySearch() {
    }

    static int binarySearch(int[] sortedValues, int target) {
        validateInput(sortedValues);

        int low = 0;
        int high = sortedValues.length - 1;

        while (low <= high) {
            int mid = midpoint(low, high);

            if (sortedValues[mid] == target) {
                return mid;
            }

            if (sortedValues[mid] < target) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        return NOT_FOUND;
    }

    private static void validateInput(int[] sortedValues) {
        if (sortedValues == null) {
            throw new IllegalArgumentException("Input array cannot be null");
        }
    }

    private static int midpoint(int low, int high) {
        return low + (high - low) / 2;
    }

    private static String formatSearchResult(int index) {
        if (index == NOT_FOUND) {
            return "Element is not present in array";
        }

        return "Element is present at index " + index;
    }

    private static void printSearchResult(int index) {
        System.out.println(formatSearchResult(index));
    }

    private static int[] demoValues() {
        return new int[] {2, 3, 4, 10, 40};
    }

    public static void main(String[] args) {
        printSearchResult(binarySearch(demoValues(), DEMO_TARGET));
    }
}
