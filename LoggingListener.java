/**
 * Logging listener for computation events.
 */
public class LoggingListener extends AbstractListener {

    @Override
    protected void handleComputationStart(int arraySize) {
        System.out.println("[INFO] Starting computation for array of size " + arraySize);
    }

    @Override
    protected void handleComputationComplete(PrefixSum.PrefixSumResult result) {
        System.out.println("[INFO] Computation completed with " + result.getInputSize() +
                           " elements, sum=" + result.getTotalSum());
    }

    @Override
    protected void handleComputationError(Exception exception) {
        System.out.println("[ERROR] Computation failed: " + exception.getMessage());
    }
}
