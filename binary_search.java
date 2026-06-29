final class BinarySearch {
    private static final int NOT_FOUND = -1;

    private BinarySearch() {
    }

    static int binarySearch(int[] sortedValues, int target) {
        int left = 0;
        int right = sortedValues.length - 1;

        while (left <= right) {
            int middle = left + (right - left) / 2;
            int value = sortedValues[middle];

            if (value == target) {
                return middle;
            }

            if (value < target) {
                left = middle + 1;
            } else {
                right = middle - 1;
            }
        }

        return NOT_FOUND;
    }

    public static void main(String[] args) {
        int[] values = { 2, 3, 4, 10, 40 };
        int target = 10;
        int result = binarySearch(values, target);

        printResult(result);
    }

    private static void printResult(int index) {
        if (index == NOT_FOUND) {
            System.out.println("Element is not present in array");
        } else {
            System.out.println("Element is present at index " + index);
        }
    }
}
