/**
 * Strategy interface for different binary search variants.
 *
 * @param <T> the type of elements to compare
 */
interface SearchStrategy<T extends Comparable<T>> {
    /**
     * Process comparison result and update bounds.
     *
     * @param comparison result of comparison (0, <0, >0)
     * @param mid current middle index
     * @return true to continue searching, false if match found
     */
    boolean processBoundary(int comparison, int mid);

    /**
     * Get the final result index.
     *
     * @return the index to return, or -1 if not found
     */
    int getResult();
}

/**
 * Standard binary search strategy - returns first match found.
 */
class StandardSearchStrategy<T extends Comparable<T>> implements SearchStrategy<T> {
    private int low;
    private int high;
    private int result = -1;

    StandardSearchStrategy(int low, int high) {
        this.low = low;
        this.high = high;
    }

    @Override
    public boolean processBoundary(int comparison, int mid) {
        if (comparison == 0) {
            result = mid;
            return false;
        } else if (comparison < 0) {
            low = mid + 1;
        } else {
            high = mid - 1;
        }
        return true;
    }

    @Override
    public int getResult() {
        return result;
    }

    int getLow() {
        return low;
    }

    int getHigh() {
        return high;
    }
}

/**
 * Search strategy that finds the first (leftmost) occurrence.
 */
class FirstOccurrenceStrategy<T extends Comparable<T>> implements SearchStrategy<T> {
    private int low;
    private int high;
    private int result = -1;

    FirstOccurrenceStrategy(int low, int high) {
        this.low = low;
        this.high = high;
    }

    @Override
    public boolean processBoundary(int comparison, int mid) {
        if (comparison == 0) {
            result = mid;
            high = mid - 1;
            return true;
        } else if (comparison < 0) {
            low = mid + 1;
        } else {
            high = mid - 1;
        }
        return true;
    }

    @Override
    public int getResult() {
        return result;
    }

    int getLow() {
        return low;
    }

    int getHigh() {
        return high;
    }
}
