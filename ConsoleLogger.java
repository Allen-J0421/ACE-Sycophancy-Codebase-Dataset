import java.io.PrintStream;
import java.util.Objects;

/**
 * A {@link CycleDetectionLogger} that writes human-readable lines to a
 * {@link PrintStream} (by default {@link System#out}). Depends only on the JDK.
 */
final class ConsoleLogger implements CycleDetectionLogger {

    private final PrintStream out;

    ConsoleLogger() {
        this(System.out);
    }

    ConsoleLogger(PrintStream out) {
        this.out = Objects.requireNonNull(out, "out");
    }

    @Override
    public void detectionStarted(String algorithm, DirectedGraph graph) {
        out.printf("[%s] detection started on graph with %d vertices%n", algorithm, graph.vertices());
    }

    @Override
    public void cycleFound(String algorithm, Cycle cycle) {
        out.printf("[%s] cycle found: %s%n", algorithm, cycle);
    }

    @Override
    public void cycleDetected(String algorithm) {
        out.printf("[%s] cycle detected%n", algorithm);
    }

    @Override
    public void noCycleFound(String algorithm) {
        out.printf("[%s] no cycle found (acyclic)%n", algorithm);
    }
}
