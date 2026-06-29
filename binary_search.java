class BinarySearch {
    static final int NOT_FOUND = -1;

    static int binarySearch(int[] array, int target) {
        int low = 0;
        int high = array.length - 1;

        while (low <= high) {
            int mid = low + (high - low) / 2;

            if (array[mid] == target) {
                return mid;
            }

            if (array[mid] < target) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        return NOT_FOUND;
    }

    public static void main(String[] args) {
        BinarySearchDemo.run();
    }
}

final class BinarySearchDemo {
    private static final int[] SAMPLE_ARRAY = { 2, 3, 4, 10, 40 };
    private static final int SAMPLE_TARGET = 10;

    private BinarySearchDemo() {
    }

    static void run() {
        int result = BinarySearch.binarySearch(SAMPLE_ARRAY, SAMPLE_TARGET);

        System.out.println(SearchResultFormatter.format(result));
    }
}

final class SearchResultFormatter {
    private SearchResultFormatter() {
    }

    static String format(int index) {
        if (index == BinarySearch.NOT_FOUND) {
            return "Element is not present in array";
        }

        return "Element is present at index " + index;
    }
}
