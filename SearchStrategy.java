/**
 * Strategy interface for different binary search variants.
 * Delegates boundary handling to a BoundaryHandler.
 *
 * @param <T> the type of elements to compare
 */
interface SearchStrategy<T extends Comparable<T>> {
    /**
     * Process comparison result and update bounds.
     * Returns true to continue searching, false to stop.
     *
     * @param comparison result of comparison (0, <0, >0)
     * @param mid current middle index
     * @return true to continue, false to terminate
     */
    boolean processBoundary(int comparison, int mid);

    /**
     * Get the final result index.
     *
     * @return the index to return, or -1 if not found
     */
    int getResult();

    /**
     * Check if search should continue.
     *
     * @return true if there are more elements to search
     */
    boolean isValid();

    /**
     * Get lower bound.
     */
    int getLow();

    /**
     * Get upper bound.
     */
    int getHigh();
}

/**
 * Abstract base strategy consolidating common behavior.
 */
abstract class BaseSearchStrategy<T extends Comparable<T>> implements SearchStrategy<T> {
    protected final BoundaryHandler handler;

    protected BaseSearchStrategy(BoundaryHandler handler) {
        this.handler = handler;
    }

    @Override
    public boolean processBoundary(int comparison, int mid) {
        return handler.handleComparison(comparison, mid);
    }

    @Override
    public int getResult() {
        return handler.getResult();
    }

    @Override
    public boolean isValid() {
        return handler.isValid();
    }

    @Override
    public int getLow() {
        return handler.getLow();
    }

    @Override
    public int getHigh() {
        return handler.getHigh();
    }
}

/**
 * Standard binary search strategy - returns first match found.
 */
class StandardSearchStrategy<T extends Comparable<T>> extends BaseSearchStrategy<T> {
    StandardSearchStrategy(int low, int high) {
        super(new StandardBoundaryHandler(low, high));
    }
}

/**
 * Search strategy that finds the first (leftmost) occurrence.
 */
class FirstOccurrenceStrategy<T extends Comparable<T>> extends BaseSearchStrategy<T> {
    FirstOccurrenceStrategy(int low, int high) {
        super(new FirstOccurrenceBoundaryHandler(low, high));
    }
}

/**
 * Search strategy that finds the last (rightmost) occurrence.
 */
class LastOccurrenceStrategy<T extends Comparable<T>> extends BaseSearchStrategy<T> {
    LastOccurrenceStrategy(int low, int high) {
        super(new LastOccurrenceBoundaryHandler(low, high));
    }
}
