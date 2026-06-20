/**
 * Dependency-free logging facade for cycle-detection events. The library never
 * logs on its own; attach a logger to a detector via
 * {@link CycleDetector#withLogging(CycleDetectionLogger)} to observe events.
 *
 * <p>Two implementations ship with the library — {@link NoOpLogger} (silent)
 * and {@link ConsoleLogger}. To route events into {@code java.util.logging},
 * SLF4J, Log4j, or any other framework, implement this interface in your own
 * code: the library itself depends only on the JDK, so the choice of logging
 * backend stays entirely with the caller.
 *
 * <p>The {@code algorithm} argument is the detector's {@link CycleDetector#name()}.
 */
interface CycleDetectionLogger {

    /** An algorithm has begun scanning {@code graph}. */
    void detectionStarted(String algorithm, DirectedGraph graph);

    /** A cycle was located, including its vertex path. Emitted by {@code findCycle}. */
    void cycleFound(String algorithm, Cycle cycle);

    /** A cycle's existence was confirmed without reconstructing its path. Emitted by {@code hasCycle}. */
    void cycleDetected(String algorithm);

    /** The graph was determined to be acyclic. */
    void noCycleFound(String algorithm);

    /** Returns the shared silent logger that discards every event. */
    static CycleDetectionLogger noOp() {
        return NoOpLogger.INSTANCE;
    }
}
