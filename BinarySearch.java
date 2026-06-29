public class BinarySearch {
    public static final int NOT_FOUND = -1;

    private BinarySearch() {
    }

    public static int binarySearch(int[] values, int target) {
        return search(values, target).index();
    }

    public static SearchResult search(int[] values, int target) {
        SearchBounds bounds = SearchBounds.forValues(values);

        while (bounds.hasCandidates()) {
            int mid = bounds.midpoint();

            if (values[mid] == target) {
                return SearchResult.foundAt(mid);
            }

            if (values[mid] < target) {
                bounds.discardLowerHalf(mid);
            } else {
                bounds.discardUpperHalf(mid);
            }
        }

        return SearchResult.notFound();
    }

    public static void main(String[] args) {
        BinarySearchDemo.run();
    }

    private static final class SearchBounds {
        private int low;
        private int high;

        private SearchBounds(int low, int high) {
            this.low = low;
            this.high = high;
        }

        static SearchBounds forValues(int[] values) {
            return new SearchBounds(0, values.length - 1);
        }

        boolean hasCandidates() {
            return low <= high;
        }

        int midpoint() {
            return low + (high - low) / 2;
        }

        void discardLowerHalf(int midpoint) {
            low = midpoint + 1;
        }

        void discardUpperHalf(int midpoint) {
            high = midpoint - 1;
        }
    }
}
