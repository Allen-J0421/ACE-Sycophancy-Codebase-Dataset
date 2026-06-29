final class BinarySearch {
    private static final int NOT_FOUND = -1;

    private BinarySearch() {
    }

    static int binarySearch(int[] array, int target) {
        int low = 0;
        int high = array.length - 1;

        while (low <= high) {
            int middle = low + (high - low) / 2;
            int middleValue = array[middle];

            if (middleValue == target) {
                return middle;
            }

            if (middleValue < target) {
                low = middle + 1;
            } else {
                high = middle - 1;
            }
        }

        return NOT_FOUND;
    }

    public static void main(String[] args) {
        int[] array = { 2, 3, 4, 10, 40 };
        int target = 10;
        int result = binarySearch(array, target);

        System.out.println(formatResultMessage(result));
    }

    private static String formatResultMessage(int result) {
        if (result == NOT_FOUND) {
            return "Element is not present in array";
        }

        return "Element is present at index " + result;
    }
}
