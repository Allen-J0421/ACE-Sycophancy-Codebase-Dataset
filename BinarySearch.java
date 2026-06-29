import java.util.Objects;

public final class BinarySearch {
    public static final int NOT_FOUND_INDEX = SearchResult.NOT_FOUND_INDEX;

    private BinarySearch() {
    }

    public static SearchResult search(int[] sortedValues, int target) {
        return SearchResult.fromIndex(binarySearch(sortedValues, target));
    }

    public static int binarySearch(int[] sortedValues, int target) {
        Objects.requireNonNull(sortedValues, "sortedValues");

        SearchBounds bounds = SearchBounds.forLength(sortedValues.length);

        while (bounds.hasCandidates()) {
            int middle = bounds.middleIndex();
            int candidate = sortedValues[middle];

            if (candidate == target) {
                return middle;
            }

            if (candidate < target) {
                bounds.discardLowerHalfThrough(middle);
            } else {
                bounds.discardUpperHalfFrom(middle);
            }
        }

        return NOT_FOUND_INDEX;
    }

    public static void main(String[] args) {
        BinarySearchDemo.run();
    }
}
