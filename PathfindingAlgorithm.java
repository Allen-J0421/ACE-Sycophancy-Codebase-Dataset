/**
 * Strategy interface for different shortest path algorithms.
 *
 * Enables pluggable algorithm implementations while maintaining
 * consistent API and result format.
 *
 * Implementations:
 * - DijkstraShortestPathSolver: O((V+E)log V), requires non-negative weights
 * - BellmanFordSolver: O(VE), handles negative weights
 * - Future: A* (with heuristic), Floyd-Warshall, etc.
 *
 * Example:
 * {@code
 * PathfindingAlgorithm dijkstra = new DijkstraShortestPathSolver();
 * PathfindingAlgorithm bellmanFord = new BellmanFordSolver();
 *
 * // Both work with same interface
 * ShortestPathResult result1 = dijkstra.solve(graph, 0);
 * ShortestPathResult result2 = bellmanFord.solve(graph, 0);
 * }
 *
 * @see DijkstraShortestPathSolver
 * @see BellmanFordSolver
 */
interface PathfindingAlgorithm {
    /**
     * Computes shortest paths from source to all vertices.
     *
     * @param graph The weighted graph to search (non-null)
     * @param sourceNode The source vertex index (must be valid)
     * @return ShortestPathResult containing distances, paths, and metadata
     * @throws NullPointerException if graph is null
     * @throws IllegalArgumentException if sourceNode is out of bounds
     * @throws IllegalArgumentException if algorithm constraints violated
     *         (e.g., negative weights for Dijkstra)
     */
    ShortestPathResult solve(WeightedGraphView graph, int sourceNode);

    /**
     * Human-readable name of the algorithm.
     *
     * @return Algorithm name (e.g., "Dijkstra", "Bellman-Ford")
     */
    String getName();
}
