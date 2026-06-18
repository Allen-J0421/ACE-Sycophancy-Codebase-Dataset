import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Decorator that adds observer notifications to any Graph.
 *
 * Enables real-time monitoring of graph modifications without
 * modifying Graph class or coupling to observer implementations.
 *
 * Observer Pattern: Decouples graph from observers
 * Decorator Pattern: Adds observability without inheritance
 *
 * Example:
 * {@code
 * Graph graph = Graph.create(5);
 * ObservableGraph observable = new ObservableGraph(graph);
 *
 * GraphObserver logger = new LoggingGraphObserver();
 * GraphObserver validator = new ValidationGraphObserver();
 * observable.subscribe(logger);
 * observable.subscribe(validator);
 *
 * observable.addEdge(0, 1, 4);  // Notifies all observers
 * }
 *
 * @see GraphObserver
 * @see Graph
 */
class ObservableGraph implements WeightedGraphView {
    private final Graph delegate;
    private final List<GraphObserver> observers;

    ObservableGraph(Graph graph) {
        this.delegate = Objects.requireNonNull(graph);
        this.observers = new ArrayList<>();
    }

    /**
     * Registers an observer to receive graph modification events.
     *
     * @param observer Observer to subscribe (non-null)
     */
    void subscribe(GraphObserver observer) {
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
    boolean unsubscribe(GraphObserver observer) {
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
     * Adds edge and notifies all observers.
     *
     * @param source Source vertex
     * @param destination Destination vertex
     * @param weight Edge weight
     */
    void addEdge(int source, int destination, int weight) {
        delegate.addEdge(source, destination, weight);
        notifyEdgeAdded(source, destination, weight);
    }

    @Override
    public List<Edge> getAdjacencyListFor(int vertex) {
        return delegate.getAdjacencyListFor(vertex);
    }

    @Override
    public int getVertexCount() {
        return delegate.getVertexCount();
    }

    private void notifyEdgeAdded(int source, int destination, int weight) {
        for (GraphObserver observer : observers) {
            observer.onEdgeAdded(source, destination, weight);
        }
    }

    @Override
    public String toString() {
        return String.format("ObservableGraph(%s, observers=%d)", delegate, observers.size());
    }
}
