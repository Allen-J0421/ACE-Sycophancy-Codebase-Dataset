/**
 * Observer interface for graph modification events.
 *
 * Observers are notified when the graph structure changes,
 * enabling logging, visualization, and analysis without
 * coupling graph logic to side-effect-heavy systems.
 *
 * Example:
 * {@code
 * Graph graph = Graph.create(5);
 * GraphObserver logger = new LoggingGraphObserver();
 * graph.subscribe(logger);
 *
 * graph.addEdge(0, 1, 4);  // Triggers: onEdgeAdded(0, 1, 4)
 * }
 *
 * @see Graph
 * @see LoggingGraphObserver
 */
interface GraphObserver {
    /**
     * Called when an edge is added to the graph.
     *
     * @param source Source vertex
     * @param destination Destination vertex
     * @param weight Edge weight
     */
    void onEdgeAdded(int source, int destination, int weight);

    /**
     * Called when graph creation is initiated.
     *
     * @param vertexCount Number of vertices in graph
     */
    void onGraphCreated(int vertexCount);
}
