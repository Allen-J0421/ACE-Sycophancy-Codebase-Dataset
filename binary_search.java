final class BinarySearch {
    private static final int NOT_FOUND = -1;
    private static final SearchDemo SAMPLE_DEMO = new SearchDemo(new int[] { 2, 3, 4, 10, 40 }, 10);

    private BinarySearch() {
    }

    static int binarySearch(int[] arr, int target) {
        return toLegacyIndex(search(arr, target));
    }

    public static void main(String[] args) {
        System.out.println(SAMPLE_DEMO.run());
    }

    private static SearchResult search(int[] arr, int target) {
        return search(arr, target, SearchRange.forArray(arr));
    }

    private static SearchResult search(int[] arr, int target, SearchRange range) {
        if (!range.isValid()) {
            return SearchResult.notFound();
        }

        int middleIndex = range.middleIndex();
        int middleValue = arr[middleIndex];

        if (middleValue == target) {
            return SearchResult.foundAt(middleIndex);
        }

        if (middleValue < target) {
            return search(arr, target, range.discardLowerHalf(middleIndex));
        }

        return search(arr, target, range.discardUpperHalf(middleIndex));
    }

    private static int toLegacyIndex(SearchResult result) {
        return result.wasFound() ? result.index() : NOT_FOUND;
    }

    private static String formatSearchResult(SearchResult result) {
        return result.wasFound()
                ? "Element is present at index " + result.index()
                : "Element is not present in array";
    }

    private record SearchRange(int low, int high) {
        private static SearchRange forArray(int[] arr) {
            return new SearchRange(0, arr.length - 1);
        }

        private boolean isValid() {
            return low <= high;
        }

        private int middleIndex() {
            return low + (high - low) / 2;
        }

        private SearchRange discardLowerHalf(int middleIndex) {
            return new SearchRange(middleIndex + 1, high);
        }

        private SearchRange discardUpperHalf(int middleIndex) {
            return new SearchRange(low, middleIndex - 1);
        }
    }

    private record SearchResult(int index) {
        private static SearchResult foundAt(int index) {
            return new SearchResult(index);
        }

        private static SearchResult notFound() {
            return new SearchResult(NOT_FOUND);
        }

        private boolean wasFound() {
            return index != NOT_FOUND;
        }
    }

    private record SearchDemo(int[] values, int target) {
        private String run() {
            return formatSearchResult(search(values, target));
        }
    }
}
