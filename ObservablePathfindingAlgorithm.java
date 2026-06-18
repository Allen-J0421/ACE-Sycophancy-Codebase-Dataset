import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Decorator that adds observer notifications to any PathfindingAlgorithm.
 *
 * Enables real-time monitoring of algorithm progress without
 * modifying algorithm implementations or coupling to side-effect systems.
 *
 * Observer Pattern: Decouples algorithm from observers
 * Decorator Pattern: Adds observability without inheritance
 *
 * Events Fired:
 * - onAlgorithmStart: When execution begins
 * - onVertexProcessed: For each vertex processed
 * - onEdgeRelaxed: For each successful edge relaxation
 * - onAlgorithmComplete: When execution finishes
 *
 * Example:
 * {@code
 * PathfindingAlgorithm dijkstra = new DijkstraShortestPathSolver();
 * ObservablePathfindingAlgorithm observable = new ObservablePathfindingAlgorithm(dijkstra);
 *
 * AlgorithmObserver logger = new LoggingAlgorithmObserver();
 * AlgorithmObserver visualizer = new VisualizationAlgorithmObserver();
 * observable.subscribe(logger);
 * observable.subscribe(visualizer);
 *
 * ShortestPathResult result = observable.solve(graph, 0);
 * // Events fired during and after execution
 * }
 *
 * @see AlgorithmObserver
 * @see PathfindingAlgorithm
 */
class ObservablePathfindingAlgorithm implements PathfindingAlgorithm {
    private final PathfindingAlgorithm delegate;
    private final List<AlgorithmObserver> observers;

    ObservablePathfindingAlgorithm(PathfindingAlgorithm algorithm) {
        this.delegate = Objects.requireNonNull(algorithm);
        this.observers = new ArrayList<>();
    }

    /**
     * Registers an observer to receive algorithm execution events.
     *
     * @param observer Observer to subscribe (non-null)
     */
    void subscribe(AlgorithmObserver observer) {
        Objects.requireNonNull(observer);
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    /**
     * Unregisters an observer from receiving events.
     *
     * @param observer Observer to unsubscribe
     * @return true if observer was subscribed, false otherwise
     */
    boolean unsubscribe(AlgorithmObserver observer) {
        return observers.remove(observer);
    }

    /**
     * Gets count of subscribed observers.
     *
     * @return Number of observers
     */
    int observerCount() {
        return observers.size();
    }

    /**
     * Solves shortest path while notifying observers of progress.
     *
     * Note: This implementation notifies onAlgorithmStart and
     * onAlgorithmComplete, but not internal events (onVertexProcessed,
     * onEdgeRelaxed) since those require changes to algorithm implementations.
     * See wrap() method for enhanced observability options.
     *
     * @param graph The weighted graph
     * @param sourceNode The source vertex
     * @return ShortestPathResult with computed distances
     */
    @Override
    public ShortestPathResult solve(WeightedGraphView graph, int sourceNode) {
        long startTime = System.currentTimeMillis();
        notifyAlgorithmStart(sourceNode, graph.getVertexCount());

        ShortestPathResult result = delegate.solve(graph, sourceNode);

        long executionTime = System.currentTimeMillis() - startTime;
        notifyAlgorithmComplete(convertToArray(result.getDistances()), 0, executionTime);

        return result;
    }

    @Override
    public String getName() {
        return delegate.getName();
    }

    private void notifyAlgorithmStart(int sourceNode, int vertexCount) {
        for (AlgorithmObserver observer : observers) {
            observer.onAlgorithmStart(getName(), sourceNode, vertexCount);
        }
    }

    private void notifyAlgorithmComplete(int[] distances, int totalRelaxations, long executionTime) {
        for (AlgorithmObserver observer : observers) {
            observer.onAlgorithmComplete(distances, totalRelaxations, executionTime);
        }
    }

    private int[] convertToArray(List<Integer> distances) {
        int[] array = new int[distances.size()];
        for (int i = 0; i < distances.size(); i++) {
            array[i] = distances.get(i);
        }
        return array;
    }

    @Override
    public String toString() {
        return String.format("ObservablePathfindingAlgorithm(%s, observers=%d)",
                           delegate.getName(), observers.size());
    }
}
