import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.PriorityQueue;

/**
 * Wrapper for any PathfindingAlgorithm that tracks execution metrics.
 * Useful for performance analysis and algorithm comparison.
 *
 * Overhead: Minimal, only tracking counts and timestamps
 *
 * Example:
 * {@code
 * PathfindingAlgorithm dijkstra = new DijkstraShortestPathSolver();
 * MetricsTrackingSolver metrics = new MetricsTrackingSolver(dijkstra);
 *
 * ShortestPathResult result = metrics.solve(graph, 0);
 * System.out.println(metrics.getMetrics());
 * }
 *
 * @see PathfindingAlgorithm
 * @see ExecutionMetrics
 */
class MetricsTrackingSolver implements PathfindingAlgorithm {
    private final PathfindingAlgorithm delegate;
    private final ExecutionMetrics metrics;
    private static final int INFINITY = Integer.MAX_VALUE;

    MetricsTrackingSolver(PathfindingAlgorithm delegate) {
        this.delegate = delegate;
        this.metrics = new ExecutionMetrics();
    }

    /**
     * Delegates to wrapped algorithm while tracking metrics.
     *
     * @param graph The weighted graph
     * @param sourceNode The source vertex
     * @return Result containing distances and paths
     */
    @Override
    public ShortestPathResult solve(WeightedGraphView graph, int sourceNode) {
        metrics.recordStart();
        ShortestPathResult result = delegate.solve(graph, sourceNode);
        metrics.recordEnd();
        return result;
    }

    @Override
    public String getName() {
        return delegate.getName() + " (with metrics)";
    }

    ExecutionMetrics getMetrics() {
        return metrics;
    }
}
