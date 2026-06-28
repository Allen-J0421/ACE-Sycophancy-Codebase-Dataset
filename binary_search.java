final class BinarySearch {
    private static final int NOT_FOUND = -1;
    private static final int[] SAMPLE_VALUES = { 2, 3, 4, 10, 40 };
    private static final int SAMPLE_TARGET = 10;

    private BinarySearch() {
    }

    static int binarySearch(int[] values, int target) {
        return indexOf(values, target);
    }

    static int indexOf(int[] values, int target) {
        int low = 0;
        int high = values.length - 1;

        while (low <= high) {
            int mid = low + (high - low) / 2;
            int current = values[mid];

            if (current == target) {
                return mid;
            }

            if (current < target) {
                low = nextLowerBound(mid);
            } else {
                high = nextUpperBound(mid);
            }
        }

        return NOT_FOUND;
    }

    private static int nextLowerBound(int mid) {
        return mid + 1;
    }

    private static int nextUpperBound(int mid) {
        return mid - 1;
    }

    public static void main(String[] args) {
        int result = indexOf(SAMPLE_VALUES, SAMPLE_TARGET);

        printSearchResult(result);
    }

    private static void printSearchResult(int result) {
        if (result == NOT_FOUND) {
            System.out.println("Element is not present in array");
        } else {
            System.out.println("Element is present at index " + result);
        }
    }
}
