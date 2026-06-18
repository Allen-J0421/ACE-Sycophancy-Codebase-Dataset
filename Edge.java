import java.util.Objects;

/**
 * Validates that edge weights are non-negative.
 * Used during edge creation to enforce constraint that weighted graphs
 * with Dijkstra's algorithm require non-negative weights.
 */
class EdgeWeightValidator implements Validator<Integer> {
    private static final String ERROR_MESSAGE = "Edge weight must be non-negative, got %d";

    @Override
    public void validate(Integer weight) throws IllegalArgumentException {
        if (weight < 0) {
            throw new IllegalArgumentException(String.format(ERROR_MESSAGE, weight));
        }
    }
}

/**
 * Represents a weighted edge in an undirected graph.
 * Immutable value object with non-negative weight constraint.
 *
 * Example:
 * {@code
 * Edge edge = Edge.of(targetNode, 10);
 * int target = edge.getDestination();  // 10
 * int weight = edge.getWeight();       // 10
 * }
 *
 * @see WeightedGraphView
 */
class Edge {
    private final int destination;
    private final int weight;
    private static final EdgeWeightValidator WEIGHT_VALIDATOR = new EdgeWeightValidator();

    private Edge(int destination, int weight) {
        WEIGHT_VALIDATOR.validate(weight);
        this.destination = destination;
        this.weight = weight;
    }

    static Edge of(int destination, int weight) {
        return new Edge(destination, weight);
    }

    int getDestination() {
        return destination;
    }

    int getWeight() {
        return weight;
    }

    @Override
    public String toString() {
        return String.format("Edge(to=%d, weight=%d)", destination, weight);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Edge)) return false;
        Edge other = (Edge) obj;
        return destination == other.destination && weight == other.weight;
    }

    @Override
    public int hashCode() {
        return Objects.hash(destination, weight);
    }
}
