import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Solves single-source shortest path using Bellman-Ford algorithm.
 *
 * Algorithm Overview:
 * - Relaxes all edges V-1 times in sequence
 * - Detects negative weight cycles
 * - Works with negative weights (unlike Dijkstra)
 *
 * Time Complexity: O(VE) where V = vertices, E = edges
 * Space Complexity: O(V) for distances and predecessors
 *
 * Constraints:
 * - Cannot handle negative cycles (returns false if detected)
 * - Slower than Dijkstra for non-negative weights
 * - Better for general case with potential negative edges
 *
 * Use Cases:
 * - Graphs with negative edge weights
 * - Currency arbitrage detection
 * - Network routing protocols
 *
 * Example:
 * {@code
 * Graph graph = GraphBuilder.withVertexCount(5)
 *     .addEdge(0, 1, 4)
 *     .addEdge(0, 2, 8)
 *     .addEdge(1, 2, -3)  // Negative weight (allowed here)
 *     .build();
 * BellmanFordSolver solver = new BellmanFordSolver();
 * ShortestPathResult result = solver.solve(graph, 0);
 * if (solver.hasNegativeCycle()) {
 *     System.out.println("Negative cycle detected");
 * }
 * }
 *
 * @see ShortestPathResult
 * @see Path
 * @see PathfindingAlgorithm
 * @see DijkstraShortestPathSolver
 */
class BellmanFordSolver implements PathfindingAlgorithm {
    private static final int INFINITY = Integer.MAX_VALUE;
    private boolean hasNegativeCycle;

    BellmanFordSolver() {
        this.hasNegativeCycle = false;
    }

    /**
     * Computes shortest paths from source to all vertices.
     * Returns result even if negative cycle detected, but caller
     * should check hasNegativeCycle() for result validity.
     *
     * @param graph The weighted graph (non-null)
     * @param sourceNode The source vertex (must be valid)
     * @return ShortestPathResult with computed distances and paths
     * @throws NullPointerException if graph is null
     * @throws IllegalArgumentException if sourceNode is out of bounds
     */
    public ShortestPathResult solve(WeightedGraphView graph, int sourceNode) {
        Objects.requireNonNull(graph, "Graph cannot be null");
        validateSourceNode(graph, sourceNode);

        int vertexCount = graph.getVertexCount();
        int[] distances = initializeDistances(vertexCount, sourceNode);
        int[] predecessors = initializePredecessors(vertexCount);
        this.hasNegativeCycle = false;

        // Relax edges V-1 times
        for (int i = 0; i < vertexCount - 1; i++) {
            for (int u = 0; u < vertexCount; u++) {
                for (Edge edge : graph.getAdjacencyListFor(u)) {
                    relaxEdge(distances, predecessors, u, edge);
                }
            }
        }

        // Check for negative cycles
        detectNegativeCycle(graph, distances);

        List<Integer> distanceList = convertToList(distances);
        return ShortestPathResult.of(distanceList, predecessors, sourceNode, graph);
    }

    /**
     * Checks if a negative weight cycle exists in the graph.
     *
     * @return true if negative cycle detected, false otherwise
     */
    public boolean hasNegativeCycle() {
        return hasNegativeCycle;
    }

    private void relaxEdge(int[] distances, int[] predecessors, int u, Edge edge) {
        int v = edge.getDestination();
        int weight = edge.getWeight();

        // Skip if source unreachable
        if (distances[u] == INFINITY) {
            return;
        }

        int newDistance = distances[u] + weight;
        if (newDistance < distances[v]) {
            distances[v] = newDistance;
            predecessors[v] = u;
        }
    }

    private void detectNegativeCycle(WeightedGraphView graph, int[] distances) {
        for (int u = 0; u < graph.getVertexCount(); u++) {
            if (distances[u] == INFINITY) {
                continue;
            }

            for (Edge edge : graph.getAdjacencyListFor(u)) {
                int v = edge.getDestination();
                int weight = edge.getWeight();
                int newDistance = distances[u] + weight;

                if (newDistance < distances[v]) {
                    this.hasNegativeCycle = true;
                    return;
                }
            }
        }
    }

    private int[] initializeDistances(int vertexCount, int sourceNode) {
        int[] distances = new int[vertexCount];
        Arrays.fill(distances, INFINITY);
        distances[sourceNode] = 0;
        return distances;
    }

    private int[] initializePredecessors(int vertexCount) {
        int[] predecessors = new int[vertexCount];
        Arrays.fill(predecessors, -1);
        return predecessors;
    }

    private List<Integer> convertToList(int[] distances) {
        return new ArrayList<>(Arrays.asList(
            Arrays.stream(distances).boxed().toArray(Integer[]::new)
        ));
    }

    private void validateSourceNode(WeightedGraphView graph, int sourceNode) {
        if (sourceNode < 0 || sourceNode >= graph.getVertexCount()) {
            throw new IllegalArgumentException(
                String.format("Source node %d is invalid for graph with %d vertices",
                             sourceNode, graph.getVertexCount())
            );
        }
    }

    @Override
    public String getName() {
        return "Bellman-Ford";
    }
}
