/**
 * Logging listener for computation events.
 */
public class LoggingListener implements ComputationListener {

    @Override
    public void onComputationStart(int arraySize) {
        System.out.println("[INFO] Starting computation for array of size " + arraySize);
    }

    @Override
    public void onComputationComplete(PrefixSum.PrefixSumResult result) {
        System.out.println("[INFO] Computation completed: " + result);
    }

    @Override
    public void onComputationError(Exception exception) {
        System.out.println("[ERROR] Computation failed: " + exception.getMessage());
    }
}
