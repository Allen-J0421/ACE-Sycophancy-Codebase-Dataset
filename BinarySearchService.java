class BinarySearchService<T extends Comparable<? super T>> implements SearchAlgorithm<T> {
    @Override
    public int search(T[] sortedValues, T target) {
        if (sortedValues == null) {
            throw new IllegalArgumentException("sortedValues must not be null");
        }
        if (target == null) {
            throw new IllegalArgumentException("target must not be null");
        }

        int low = 0;
        int high = sortedValues.length - 1;

        while (low <= high) {
            int mid = low + (high - low) / 2;
            T midValue = sortedValues[mid];

            if (midValue == null) {
                throw new IllegalArgumentException("sortedValues must not contain null elements");
            }

            int comparison = midValue.compareTo(target);
            if (comparison == 0) {
                return mid;
            }

            if (comparison < 0) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        return -1;
    }
}
