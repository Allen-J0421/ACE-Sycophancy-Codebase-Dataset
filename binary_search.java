class BinarySearch {
    private static final int NOT_FOUND = -1;

    static int binarySearch(int[] sortedNumbers, int target) {
        int low = 0;
        int high = sortedNumbers.length - 1;

        while (low <= high) {
            int middle = low + (high - low) / 2;

            if (sortedNumbers[middle] == target) {
                return middle;
            }

            if (sortedNumbers[middle] < target) {
                low = middle + 1;
            } else {
                high = middle - 1;
            }
        }

        return NOT_FOUND;
    }

    private static String formatSearchResult(int index) {
        if (index == NOT_FOUND) {
            return "Element is not present in array";
        }

        return "Element is present at index " + index;
    }

    public static void main(String[] args) {
        final int[] sortedNumbers = { 2, 3, 4, 10, 40 };
        final int target = 10;
        final int result = binarySearch(sortedNumbers, target);

        System.out.println(formatSearchResult(result));
    }
}
