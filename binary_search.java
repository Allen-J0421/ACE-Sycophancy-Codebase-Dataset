class BinarySearch {
    private static final int NOT_FOUND = -1;

    static int binarySearch(int[] sortedValues, int target) {
        int low = 0;
        int high = sortedValues.length - 1;

        while (low <= high) {
            int mid = midpoint(low, high);
            int comparison = Integer.compare(sortedValues[mid], target);

            if (comparison == 0) {
                return mid;
            }

            if (comparison < 0) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        return NOT_FOUND;
    }

    private static int midpoint(int low, int high) {
        return low + (high - low) / 2;
    }

    private static void printSearchResult(int result) {
        if (result == NOT_FOUND) {
            System.out.println("Element is not present in array");
        } else {
            System.out.println("Element is present at index " + result);
        }
    }

    public static void main(String[] args) {
        int[] sortedValues = { 2, 3, 4, 10, 40 };
        int target = 10;
        int result = binarySearch(sortedValues, target);

        printSearchResult(result);
    }
}
