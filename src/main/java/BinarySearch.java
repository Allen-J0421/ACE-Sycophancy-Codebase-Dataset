import java.util.Objects;

final class BinarySearch {
    static final int NOT_FOUND = -1;

    private BinarySearch() {
    }

    /**
     * Returns the index of the target in a sorted array, or {@link #NOT_FOUND}.
     * When duplicates exist, this returns the first matching index.
     */
    static int indexOf(int[] sortedArray, int target) {
        int firstCandidate = insertionPoint(sortedArray, target);

        if (firstCandidate == sortedArray.length || sortedArray[firstCandidate] != target) {
            return NOT_FOUND;
        }
        return firstCandidate;
    }

    static boolean contains(int[] sortedArray, int target) {
        return indexOf(sortedArray, target) != NOT_FOUND;
    }

    static SearchResult search(int[] sortedArray, int target) {
        int index = indexOf(sortedArray, target);

        if (index == NOT_FOUND) {
            return SearchResult.notFound();
        }
        return SearchResult.foundAt(index);
    }

    static int insertionPoint(int[] sortedArray, int target) {
        Objects.requireNonNull(sortedArray, "sortedArray");

        int left = 0;
        int right = sortedArray.length;

        while (left < right) {
            int middle = left + (right - left) / 2;
            int middleValue = sortedArray[middle];

            if (middleValue < target) {
                left = middle + 1;
            } else {
                right = middle;
            }
        }

        return left;
    }
}
