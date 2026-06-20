/**
 * A {@link CycleDetectionLogger} that discards every event. This is the library
 * default, so detection has zero logging overhead unless a logger is attached.
 */
enum NoOpLogger implements CycleDetectionLogger {

    INSTANCE;

    @Override
    public void detectionStarted(String algorithm, DirectedGraph graph) {
    }

    @Override
    public void cycleFound(String algorithm, Cycle cycle) {
    }

    @Override
    public void cycleDetected(String algorithm) {
    }

    @Override
    public void noCycleFound(String algorithm) {
    }
}
