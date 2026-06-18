import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Factory for creating pathfinding algorithm instances.
 *
 * Centralizes algorithm instantiation and registration,
 * enabling pluggable algorithms without modifying client code.
 *
 * Registered Algorithms:
 * - "dijkstra" or "Dijkstra": Dijkstra's algorithm
 * - "bellman-ford" or "Bellman-Ford": Bellman-Ford algorithm
 *
 * Example:
 * {@code
 * PathfindingAlgorithm dijkstra = AlgorithmFactory.create("dijkstra");
 * PathfindingAlgorithm bellmanFord = AlgorithmFactory.create("bellman-ford");
 *
 * Optional<PathfindingAlgorithm> algo = AlgorithmFactory.createOptional("unknown");
 * algo.ifPresentOrElse(
 *     a -> System.out.println("Using: " + a.getName()),
 *     () -> System.out.println("Algorithm not found")
 * );
 *
 * // List available algorithms
 * AlgorithmFactory.listAlgorithms();  // [Dijkstra, Bellman-Ford]
 * }
 *
 * @see PathfindingAlgorithm
 * @see DijkstraShortestPathSolver
 * @see BellmanFordSolver
 */
class AlgorithmFactory {
    private static final Map<String, AlgorithmProvider> REGISTRY = new HashMap<>();

    static {
        register("dijkstra", () -> new DijkstraShortestPathSolver());
        register("bellman-ford", () -> new BellmanFordSolver());
    }

    private AlgorithmFactory() {
        // Utility class
    }

    /**
     * Registers a new algorithm implementation.
     * Used for extending with custom algorithms.
     *
     * @param name Algorithm identifier (case-insensitive)
     * @param provider Factory function to create instances
     */
    static void register(String name, AlgorithmProvider provider) {
        REGISTRY.put(name.toLowerCase(), provider);
    }

    /**
     * Creates an algorithm instance by name.
     *
     * @param name Algorithm identifier (case-insensitive)
     * @return Algorithm instance
     * @throws IllegalArgumentException if algorithm not found
     */
    static PathfindingAlgorithm create(String name) {
        return createOptional(name)
            .orElseThrow(() -> new IllegalArgumentException(
                String.format("Unknown algorithm: %s. Available: %s",
                            name, listAlgorithms())
            ));
    }

    /**
     * Creates an algorithm instance by name (optional).
     *
     * @param name Algorithm identifier (case-insensitive)
     * @return Optional containing algorithm, or empty if not found
     */
    static Optional<PathfindingAlgorithm> createOptional(String name) {
        AlgorithmProvider provider = REGISTRY.get(name.toLowerCase());
        return provider != null ? Optional.of(provider.create()) : Optional.empty();
    }

    /**
     * Lists all registered algorithm names.
     *
     * @return Comma-separated algorithm names
     */
    static String listAlgorithms() {
        return String.join(", ", REGISTRY.keySet());
    }

    /**
     * Gets count of registered algorithms.
     *
     * @return Number of available algorithms
     */
    static int getCount() {
        return REGISTRY.size();
    }

    /**
     * Provider interface for algorithm factories.
     * Enables lazy instantiation of algorithms.
     */
    interface AlgorithmProvider {
        PathfindingAlgorithm create();
    }
}
