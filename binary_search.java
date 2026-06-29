final class BinarySearch {
    private BinarySearch() {
    }

    static int binarySearch(int[] values, int target) {
        return indexOf(values, target);
    }

    static int indexOf(int[] values, int target) {
        return search(values, target).indexOrNotFound();
    }

    static SearchResult search(int[] values, int target) {
        requireValues(values);

        int low = 0;
        int high = values.length - 1;

        while (low <= high) {
            int middle = midpoint(low, high);
            int currentValue = values[middle];
            int comparison = Integer.compare(currentValue, target);

            if (comparison == 0) {
                return SearchResult.found(middle);
            }

            if (comparison < 0) {
                low = middle + 1;
            } else {
                high = middle - 1;
            }
        }

        return SearchResult.notFound();
    }

    public static void main(String[] args) {
        BinarySearchDemo.main(args);
    }

    private static void requireValues(int[] values) {
        if (values == null) {
            throw new IllegalArgumentException("values must not be null");
        }
    }

    private static int midpoint(int low, int high) {
        return low + (high - low) / 2;
    }
}
