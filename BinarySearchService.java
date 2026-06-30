class BinarySearchService implements SearchAlgorithm {
    @Override
    public int search(int[] sortedValues, int target) {
        if (sortedValues == null) {
            throw new IllegalArgumentException("sortedValues must not be null");
        }

        int low = 0;
        int high = sortedValues.length - 1;

        while (low <= high) {
            int mid = low + (high - low) / 2;

            if (sortedValues[mid] == target) {
                return mid;
            }

            if (sortedValues[mid] < target) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        return -1;
    }
}
