import java.util.ArrayList;
import java.util.List;

/**
 * Composite listener that aggregates multiple listeners.
 */
public class CompositeListener implements ComputationListener {
    private final List<ComputationListener> listeners = new ArrayList<>();

    /**
     * Adds a listener to the composite.
     *
     * @param listener the listener to add
     * @return this for method chaining
     */
    public CompositeListener add(ComputationListener listener) {
        listeners.add(listener);
        return this;
    }

    /**
     * Removes a listener from the composite.
     *
     * @param listener the listener to remove
     * @return this for method chaining
     */
    public CompositeListener remove(ComputationListener listener) {
        listeners.remove(listener);
        return this;
    }

    @Override
    public void onComputationStart(int arraySize) {
        for (ComputationListener listener : listeners) {
            try {
                listener.onComputationStart(arraySize);
            } catch (Exception e) {
                System.err.println("Error in listener during start: " + e.getMessage());
            }
        }
    }

    @Override
    public void onComputationComplete(PrefixSum.PrefixSumResult result) {
        for (ComputationListener listener : listeners) {
            try {
                listener.onComputationComplete(result);
            } catch (Exception e) {
                System.err.println("Error in listener during complete: " + e.getMessage());
            }
        }
    }

    @Override
    public void onComputationError(Exception exception) {
        for (ComputationListener listener : listeners) {
            try {
                listener.onComputationError(exception);
            } catch (Exception e) {
                System.err.println("Error in listener during error: " + e.getMessage());
            }
        }
    }

    /**
     * Checks if this composite has any listeners.
     *
     * @return true if listeners exist, false otherwise
     */
    public boolean hasListeners() {
        return !listeners.isEmpty();
    }
}
