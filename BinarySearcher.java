final class BinarySearcher implements SearchAlgorithm {
    @Override
    public SearchResult search(int[] sortedArray, int target) {
        validateInput(sortedArray);

        int low = 0;
        int high = sortedArray.length - 1;

        while (low <= high) {
            int mid = low + (high - low) / 2;

            if (sortedArray[mid] == target) {
                return SearchResult.foundAt(mid);
            }

            if (sortedArray[mid] < target) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        return SearchResult.notFound();
    }

    private static void validateInput(int[] sortedArray) {
        if (sortedArray == null) {
            throw new IllegalArgumentException("sortedArray must not be null");
        }
    }
}
