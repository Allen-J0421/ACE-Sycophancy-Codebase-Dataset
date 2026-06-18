import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.stream.IntStream;

/**
 * Undirected weighted graph representation using adjacency list.
 *
 * Properties:
 * - Undirected: addEdge(a, b, w) adds edges in both directions
 * - Weighted: Each edge has non-negative integer weight
 * - Immutable after construction (no edge removal)
 *
 * Storage:
 * - Adjacency list: O(V + E) space
 * - Edge lookups: O(degree) time per vertex
 *
 * Constraints:
 * - No self-loops (edge to same vertex)
 * - No negative weights (enforced by Edge validator)
 * - Vertex count must be positive
 *
 * Example:
 * {@code
 * Graph graph = Graph.create(4);
 * graph.addEdge(0, 1, 5);
 * graph.addEdge(1, 2, 3);
 * List<Edge> neighbors = graph.getAdjacencyListFor(1);  // [0→5, 2→3]
 * }
 *
 * @see WeightedGraphView
 * @see Edge
 * @see GraphBuilder
 */
class Graph implements WeightedGraphView {
    private final List<List<Edge>> adjacencyList;
    private final int vertexCount;
    private final VertexValidator vertexValidator;
    private static final BiPredicate<Integer, Integer> NO_SELF_LOOPS = (src, dst) -> src != dst;

    private Graph(int vertexCount) {
        if (vertexCount <= 0) {
            throw new IllegalArgumentException("Vertex count must be positive");
        }
        this.vertexCount = vertexCount;
        this.vertexValidator = new VertexValidator(vertexCount);
        this.adjacencyList = new ArrayList<>();
        IntStream.range(0, vertexCount)
            .forEach(i -> adjacencyList.add(new ArrayList<>()));
    }

    static Graph create(int vertexCount) {
        return new Graph(vertexCount);
    }

    void addEdge(int source, int destination, int weight) {
        vertexValidator.validate(source);
        vertexValidator.validate(destination);
        if (!NO_SELF_LOOPS.test(source, destination)) {
            throw new IllegalArgumentException("Self-loops are not supported");
        }
        adjacencyList.get(source).add(Edge.of(destination, weight));
        adjacencyList.get(destination).add(Edge.of(source, weight));
    }

    @Override
    public List<Edge> getAdjacencyListFor(int vertex) {
        vertexValidator.validate(vertex);
        return Collections.unmodifiableList(adjacencyList.get(vertex));
    }

    @Override
    public int getVertexCount() {
        return vertexCount;
    }

    @Override
    public String toString() {
        return String.format("Graph(vertices=%d, edges=%d)", vertexCount, countEdges());
    }

    private int countEdges() {
        return adjacencyList.stream().mapToInt(List::size).sum() / 2;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Graph)) return false;
        Graph other = (Graph) obj;
        return vertexCount == other.vertexCount && adjacencyList.equals(other.adjacencyList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vertexCount, adjacencyList);
    }
}
