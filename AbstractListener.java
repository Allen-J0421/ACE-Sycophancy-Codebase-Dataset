/**
 * Abstract base class for computation listeners using template method pattern.
 */
public abstract class AbstractListener implements ComputationListener {

    @Override
    public final void onComputationStart(int arraySize) {
        beforeComputationStart(arraySize);
        handleComputationStart(arraySize);
        afterComputationStart(arraySize);
    }

    @Override
    public final void onComputationComplete(PrefixSum.PrefixSumResult result) {
        beforeComputationComplete(result);
        handleComputationComplete(result);
        afterComputationComplete(result);
    }

    @Override
    public final void onComputationError(Exception exception) {
        beforeComputationError(exception);
        handleComputationError(exception);
        afterComputationError(exception);
    }

    protected void beforeComputationStart(int arraySize) {
        // Override to add logic before start
    }

    protected abstract void handleComputationStart(int arraySize);

    protected void afterComputationStart(int arraySize) {
        // Override to add logic after start
    }

    protected void beforeComputationComplete(PrefixSum.PrefixSumResult result) {
        // Override to add logic before complete
    }

    protected abstract void handleComputationComplete(PrefixSum.PrefixSumResult result);

    protected void afterComputationComplete(PrefixSum.PrefixSumResult result) {
        // Override to add logic after complete
    }

    protected void beforeComputationError(Exception exception) {
        // Override to add logic before error
    }

    protected abstract void handleComputationError(Exception exception);

    protected void afterComputationError(Exception exception) {
        // Override to add logic after error
    }
}
