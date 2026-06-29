public class BinarySearch {
    public static final int NOT_FOUND = -1;

    private BinarySearch() {
    }

    public static int binarySearch(int[] values, int target) {
        return search(values, target).index();
    }

    public static SearchResult search(int[] values, int target) {
        int low = 0;
        int high = values.length - 1;

        while (low <= high) {
            int mid = midpoint(low, high);

            if (values[mid] == target) {
                return SearchResult.foundAt(mid);
            }

            if (values[mid] < target) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        return SearchResult.notFound();
    }

    private static int midpoint(int low, int high) {
        return low + (high - low) / 2;
    }

    public static void main(String[] args) {
        BinarySearchDemo.run();
    }
}
