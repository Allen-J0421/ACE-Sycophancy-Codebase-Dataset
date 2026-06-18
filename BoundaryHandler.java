/**
 * Handles boundary adjustments based on comparison results.
 * Defines the behavior of how low/high bounds change during search.
 */
abstract class BoundaryHandler {
    protected int low;
    protected int high;
    protected int result = -1;

    protected BoundaryHandler(int low, int high) {
        this.low = low;
        this.high = high;
    }

    /**
     * Process comparison and adjust bounds.
     * Returns true to continue searching, false to stop.
     *
     * @param comparison result of comparison (0, <0, >0)
     * @param mid the middle index
     * @return true to continue, false to terminate search
     */
    abstract boolean handleComparison(int comparison, int mid);

    public int getLow() {
        return low;
    }

    public int getHigh() {
        return high;
    }

    public int getResult() {
        return result;
    }

    public boolean isValid() {
        return low <= high;
    }
}

/**
 * Handler for standard binary search - stops at first match.
 */
class StandardBoundaryHandler extends BoundaryHandler {
    StandardBoundaryHandler(int low, int high) {
        super(low, high);
    }

    @Override
    boolean handleComparison(int comparison, int mid) {
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
}

/**
 * Handler for finding first occurrence - continues left after match.
 */
class FirstOccurrenceBoundaryHandler extends BoundaryHandler {
    FirstOccurrenceBoundaryHandler(int low, int high) {
        super(low, high);
    }

    @Override
    boolean handleComparison(int comparison, int mid) {
        if (comparison == 0) {
            result = mid;
            high = mid - 1;
        } else if (comparison < 0) {
            low = mid + 1;
        } else {
            high = mid - 1;
        }
        return true;
    }
}

/**
 * Handler for finding last occurrence - continues right after match.
 */
class LastOccurrenceBoundaryHandler extends BoundaryHandler {
    LastOccurrenceBoundaryHandler(int low, int high) {
        super(low, high);
    }

    @Override
    boolean handleComparison(int comparison, int mid) {
        if (comparison == 0) {
            result = mid;
            low = mid + 1;
        } else if (comparison < 0) {
            low = mid + 1;
        } else {
            high = mid - 1;
        }
        return true;
    }
}
