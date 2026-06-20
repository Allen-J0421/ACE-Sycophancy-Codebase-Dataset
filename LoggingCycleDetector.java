import java.util.Objects;
import java.util.Optional;

/**
 * Decorator that emits {@link CycleDetectionLogger} events around another
 * {@link CycleDetector}, keeping the logging concern out of the algorithm
 * implementations. Obtain one via {@link CycleDetector#withLogging}.
 *
 * <p>Each operation logs {@code detectionStarted} on entry and exactly one
 * outcome event ({@code cycleFound} / {@code cycleDetected} / {@code noCycleFound})
 * on exit. Results are passed through unchanged.
 */
final class LoggingCycleDetector implements CycleDetector {

    private final CycleDetector delegate;
    private final CycleDetectionLogger logger;

    LoggingCycleDetector(CycleDetector delegate, CycleDetectionLogger logger) {
        this.delegate = Objects.requireNonNull(delegate, "delegate");
        this.logger = Objects.requireNonNull(logger, "logger");
    }

    @Override
    public Optional<Cycle> findCycle(DirectedGraph graph) {
        logger.detectionStarted(name(), graph);
        Optional<Cycle> result = delegate.findCycle(graph);
        result.ifPresentOrElse(
                cycle -> logger.cycleFound(name(), cycle),
                () -> logger.noCycleFound(name()));
        return result;
    }

    @Override
    public boolean hasCycle(DirectedGraph graph) {
        logger.detectionStarted(name(), graph);
        boolean present = delegate.hasCycle(graph);
        if (present) {
            logger.cycleDetected(name());
        } else {
            logger.noCycleFound(name());
        }
        return present;
    }

    @Override
    public String name() {
        return delegate.name();
    }
}
