class BinarySearch {
    private static final int NOT_FOUND = -1;

    static int binarySearch(int[] values, int target) {
        int left = 0;
        int right = values.length - 1;

        while (left <= right) {
            int middle = left + (right - left) / 2;
            int candidate = values[middle];

            if (candidate == target) {
                return middle;
            }

            if (candidate < target) {
                left = middle + 1;
            } else {
                right = middle - 1;
            }
        }

        return NOT_FOUND;
    }

    private static String formatSearchResult(int resultIndex) {
        if (resultIndex == NOT_FOUND) {
            return "Element is not present in array";
        }

        return "Element is present at index " + resultIndex;
    }

    public static void main(String[] args) {
        int[] sortedValues = { 2, 3, 4, 10, 40 };
        int target = 10;
        int result = binarySearch(sortedValues, target);

        System.out.println(formatSearchResult(result));
    }
}
