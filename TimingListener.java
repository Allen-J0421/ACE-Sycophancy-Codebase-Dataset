/**
 * Listener that tracks computation timing.
 */
public class TimingListener extends AbstractListener {
    private long startTime;

    @Override
    protected void handleComputationStart(int arraySize) {
        startTime = System.currentTimeMillis();
    }

    @Override
    protected void handleComputationComplete(PrefixSum.PrefixSumResult result) {
        long elapsedTime = System.currentTimeMillis() - startTime;
        System.out.println("[TIMING] Computation took " + elapsedTime + "ms");
    }

    @Override
    protected void handleComputationError(Exception exception) {
        long elapsedTime = System.currentTimeMillis() - startTime;
        System.out.println("[TIMING] Computation failed after " + elapsedTime + "ms");
    }
}
