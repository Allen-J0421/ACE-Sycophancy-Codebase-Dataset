/**
 * Observer interface for algorithm execution events.
 *
 * Observers are notified of algorithm progress including:
 * - Edge relaxations (distance improvements)
 * - Vertex processing
 * - Algorithm start/completion
 *
 * Enables progress tracking, visualization, and algorithm
 * analysis without coupling pathfinding logic to UI/logging.
 *
 * Example:
 * {@code
 * PathfindingAlgorithm algorithm = new DijkstraShortestPathSolver();
 * ObservablePathfindingAlgorithm observable = new ObservablePathfindingAlgorithm(algorithm);
 * AlgorithmObserver logger = new LoggingAlgorithmObserver();
 * observable.subscribe(logger);
 *
 * observable.solve(graph, 0);  // Triggers events during execution
 * }
 *
 * @see ObservablePathfindingAlgorithm
 * @see LoggingAlgorithmObserver
 */
interface AlgorithmObserver {
    /**
     * Called when algorithm execution starts.
     *
     * @param algorithmName Name of the algorithm
     * @param sourceNode Starting vertex
     * @param vertexCount Total vertices in graph
     */
    void onAlgorithmStart(String algorithmName, int sourceNode, int vertexCount);

    /**
     * Called when a vertex is processed (dequeued from priority queue).
     *
     * @param vertex Vertex being processed
     * @param distance Current shortest distance to vertex
     */
    void onVertexProcessed(int vertex, int distance);

    /**
     * Called when an edge is relaxed (distance improved).
     *
     * @param from Source vertex
     * @param to Destination vertex
     * @param oldDistance Previous distance estimate
     * @param newDistance New improved distance
     * @param relaxationCount Total relaxations performed so far
     */
    void onEdgeRelaxed(int from, int to, int oldDistance, int newDistance, int relaxationCount);

    /**
     * Called when algorithm execution completes.
     *
     * @param finalDistances Array of final shortest distances
     * @param totalRelaxations Total edge relaxations performed
     * @param executionTimeMillis Total execution time in milliseconds
     */
    void onAlgorithmComplete(int[] finalDistances, int totalRelaxations, long executionTimeMillis);
}
