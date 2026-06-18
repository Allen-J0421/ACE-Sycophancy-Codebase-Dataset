/**
 * Observer interface for computation events.
 */
public interface ComputationListener {
    /**
     * Called when a computation starts.
     *
     * @param arraySize the size of the input array
     */
    void onComputationStart(int arraySize);

    /**
     * Called when a computation completes.
     *
     * @param result the computation result
     */
    void onComputationComplete(PrefixSum.PrefixSumResult result);

    /**
     * Called when a computation fails.
     *
     * @param exception the exception that occurred
     */
    void onComputationError(Exception exception);
}
