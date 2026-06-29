class BinarySearch {
    private static final int NOT_FOUND = -1;

    static int binarySearch(int[] sortedNumbers, int target) {
        if (sortedNumbers == null) {
            throw new IllegalArgumentException("Array must not be null");
        }

        int left = 0;
        int right = sortedNumbers.length - 1;

        while (left <= right) {
            int middle = left + (right - left) / 2;

            if (sortedNumbers[middle] == target) {
                return middle;
            }

            if (sortedNumbers[middle] < target) {
                left = middle + 1;
            } else {
                right = middle - 1;
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
        int[] numbers = { 2, 3, 4, 10, 40 };
        int target = 10;
        int result = binarySearch(numbers, target);
        System.out.println(formatSearchResult(result));
    }
}
